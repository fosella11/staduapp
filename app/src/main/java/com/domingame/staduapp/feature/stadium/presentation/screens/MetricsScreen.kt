package com.domingame.staduapp.feature.stadium.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.domingame.staduapp.feature.stadium.presentation.components.MetricCard
import com.domingame.staduapp.feature.stadium.presentation.model.StadiumUiState
import com.domingame.staduapp.ui.theme.BlockedRed
import com.domingame.staduapp.ui.theme.OccupiedGreen

@Composable
fun MetricsScreen(state: StadiumUiState) {
    if (state.stadiumState == null) return
    val metrics = state.stadiumState.metrics

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Métricas",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Top Row - Main Metrics
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCardWithIcon(
                title = "Real Procesados",
                value = "${metrics.totalAdmitted + metrics.totalRefused + metrics.totalBlocked}",
                icon = Icons.Default.CheckCircle,
                iconTint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            MetricCardWithIcon(
                title = "Asignados",
                value = "${metrics.totalAdmitted}",
                icon = Icons.Default.CheckCircle,
                iconTint = OccupiedGreen,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Second Row
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCardWithIcon(
                title = "Rechazados",
                value = "${metrics.totalRefused}",
                icon = Icons.Default.Close,
                iconTint = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
            MetricCardWithIcon(
                title = "Dist. Promedio",
                value = "%.1f".format(metrics.averageDistanceGlobal),
                icon = null,
                iconTint = Color.Transparent,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // Distance Section
        Text(
            text = "Distancia media global",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Distance Bars
        state.stadiumState.sectors.values.sortedBy { it.name.name }.forEach { sector ->
            val totalDist = sector.blocks.values.sumOf { it.accumulatedDistance }
            val totalCount = sector.blocks.values.sumOf { it.assignmentCount }
            val avg = if (totalCount > 0) totalDist.toFloat() / totalCount else 0f
            val maxDistance = 100f // Assumption for visualization
            val progress = (avg / maxDistance).coerceIn(0f, 1f)
            
            DistanceBar(
                sectorName = sector.name.name,
                distance = avg,
                progress = progress,
                percentage = "${(progress * 100).toInt()}%"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun MetricCardWithIcon(
    title: String,
    value: String,
    icon: ImageVector?,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun DistanceBar(
    sectorName: String,
    distance: Float,
    progress: Float,
    percentage: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sectorName,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = "%.1f".format(distance),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}
