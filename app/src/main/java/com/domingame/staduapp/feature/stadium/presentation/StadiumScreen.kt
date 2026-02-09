package com.domingame.staduapp.feature.stadium.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.domingame.staduapp.feature.stadium.domain.model.ConnectionState
import com.domingame.staduapp.feature.stadium.presentation.screens.DashboardScreen
import com.domingame.staduapp.feature.stadium.presentation.screens.LogScreen
import com.domingame.staduapp.feature.stadium.presentation.screens.MetricsScreen
import com.domingame.staduapp.ui.theme.ConnectedGreen
import com.domingame.staduapp.ui.theme.DisconnectedRed
import com.domingame.staduapp.ui.theme.ErrorColor
import com.domingame.staduapp.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StadiumScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val stadiumUiState by viewModel.stadiumUiState.collectAsState()
    val connectionUiState by viewModel.connectionUiState.collectAsState()

    // Lifecycle-aware connection management
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.startConnection()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.stopConnection()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Stadium Access Control",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                actions = {
                    ConnectionStatusIndicator(connectionUiState.state)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val currentRoute =
                    navController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Dashboard") },
                    label = { Text("Mapa") },
                    selected = currentRoute == "dashboard",
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Info, contentDescription = "Metrics") },
                    label = { Text("Metricas") },
                    selected = currentRoute == "metrics",
                    onClick = {
                        navController.navigate("metrics") {
                            popUpTo("dashboard") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Log") },
                    label = { Text("Registros") },
                    selected = currentRoute == "log",
                    onClick = {
                        navController.navigate("log") {
                            popUpTo("dashboard") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (stadiumUiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") { DashboardScreen(stadiumUiState) }
                    composable("metrics") { MetricsScreen(stadiumUiState) }
                    composable("log") { LogScreen(stadiumUiState) }
                }
            }
        }
    }
}

@Composable
fun ConnectionStatusIndicator(state: ConnectionState) {
    val color = when (state) {
        ConnectionState.CONNECTED -> ConnectedGreen
        ConnectionState.DISCONNECTED -> DisconnectedRed
        ConnectionState.CONNECTING -> WarningOrange
        ConnectionState.ERROR -> ErrorColor
    }

    Box(
        modifier = Modifier.padding(end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (state == ConnectionState.CONNECTING) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = color
            )
        } else {
            Row {
                //Text("") TODO : Agregar estados para conectado y probando
                Icon(
                    imageVector = Icons.Default.Info, // Or a dot icon
                    contentDescription = state.name,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

        }
    }
}
