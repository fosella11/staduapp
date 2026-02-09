package com.domingame.staduapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domingame.staduapp.ui.MainViewModel

@Composable
fun MetricsScreen(viewModel: MainViewModel) {
    val state by viewModel.stadiumState.collectAsState()
    val metrics = state.metrics

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Real-time Metrics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Cards Row 1
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Admitted", "${metrics.totalAdmitted}", Modifier.weight(1f))
            MetricCard("Refused", "${metrics.totalRefused}", Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Blocked", "${metrics.totalBlocked}", Modifier.weight(1f), isError = true)
            MetricCard("Global Avg Dist", "%.1f".format(metrics.averageDistanceGlobal), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Average Distance per Sector", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Sector List
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                state.sectors.values.sortedBy { it.name.name }.forEach { sector ->
                    val totalDist = sector.blocks.values.sumOf { it.accumulatedDistance }
                    val totalCount = sector.blocks.values.sumOf { it.assignmentCount }
                    val avg = if (totalCount > 0) totalDist.toFloat() / totalCount else 0f
                    
                    ListItem(
                        headlineContent = { Text(sector.name.name) },
                        trailingContent = { 
                            Text(
                                text = "%.1f".format(avg),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            ) 
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier, isError: Boolean = false) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}
