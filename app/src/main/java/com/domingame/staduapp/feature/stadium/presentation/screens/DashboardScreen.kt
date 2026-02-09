package com.domingame.staduapp.feature.stadium.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domingame.staduapp.feature.stadium.domain.model.SectorName
import com.domingame.staduapp.feature.stadium.presentation.components.ResponsiveSectorGrid
import com.domingame.staduapp.feature.stadium.presentation.model.StadiumUiState

@Composable
fun DashboardScreen(state: StadiumUiState) {
    if (state.stadiumState == null) return

    val totalCapacity = state.stadiumState.sectors.values.sumOf { it.totalCapacity }
    val totalAdmitted = state.stadiumState.metrics.totalAdmitted
    val globalPercentage = if (totalCapacity > 0) totalAdmitted.toFloat() / totalCapacity else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Capacidad Global Progress
        Text(
            text = "Capacidad global ($totalAdmitted/$totalCapacity)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LinearProgressIndicator(
            progress = { globalPercentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Capacidad global",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Groups of sectors as shown in Figma
        // Norte | Sur
        SectorPair(
            label1 = "Norte",
            sector1 = state.stadiumState.sectors[SectorName.NORTH],
            label2 = "Sur",
            sector2 = state.stadiumState.sectors[SectorName.SOUTH]
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Este | Oeste
        SectorPair(
            label1 = "Este",
            sector1 = state.stadiumState.sectors[SectorName.EAST],
            label2 = "Oeste",
            sector2 = state.stadiumState.sectors[SectorName.WEST]
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SectorPair(
    label1: String,
    sector1: com.domingame.staduapp.feature.stadium.domain.model.SectorState?,
    label2: String,
    sector2: com.domingame.staduapp.feature.stadium.domain.model.SectorState?
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3E5F5))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = label1,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = label2,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(Modifier.weight(1f)) {
                ResponsiveSectorGrid(sector1)
            }
            Box(Modifier.weight(1f)) {
                ResponsiveSectorGrid(sector2)
            }
        }
    }
}
