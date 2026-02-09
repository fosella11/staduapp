package com.domingame.staduapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.domingame.staduapp.domain.repository.ConnectionState
import com.domingame.staduapp.ui.MainViewModel
import com.domingame.staduapp.ui.theme.OccupiedGreen
import com.domingame.staduapp.ui.theme.WarningOrange
import com.domingame.staduapp.ui.theme.BlockedRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val connectionState by viewModel.connectionState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stadium Control") },
                actions = {
                    ConnectionStatusIndicator(connectionState)
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Dashboard") },
                    label = { Text("Map") },
                    selected = currentDestination?.route == "dashboard",
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Info, contentDescription = "Metrics") },
                    label = { Text("Metrics") },
                    selected = currentDestination?.route == "metrics",
                    onClick = {
                        navController.navigate("metrics") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "Log") },
                    label = { Text("Log") },
                    selected = currentDestination?.route == "log",
                    onClick = {
                        navController.navigate("log") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") { DashboardScreen(viewModel) }
            composable("metrics") { MetricsScreen(viewModel) }
            composable("log") { LogScreen(viewModel) }
        }
    }
}

@Composable
fun ConnectionStatusIndicator(state: ConnectionState) {
    val (color, text) = when (state) {
        ConnectionState.CONNECTED -> OccupiedGreen to "Connected"
        ConnectionState.CONNECTING -> WarningOrange to "Connecting..."
        ConnectionState.RECONNECTING -> WarningOrange to "Reconnecting..."
        ConnectionState.DISCONNECTED -> BlockedRed to "Disconnected"
        ConnectionState.ERROR -> BlockedRed to "Error"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}
