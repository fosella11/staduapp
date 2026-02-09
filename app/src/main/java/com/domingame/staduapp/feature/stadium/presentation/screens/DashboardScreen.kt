package com.domingame.staduapp.feature.stadium.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.domingame.staduapp.feature.stadium.domain.model.SectorName
import com.domingame.staduapp.feature.stadium.presentation.components.SectorCard
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
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header - Capacidad Global
        Text(
            text = "Capacidad global ($totalAdmitted/$totalCapacity)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Progress Bar
        LinearProgressIndicator(
            progress = { globalPercentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Sector Grid Title
        Text(
            text = "Capacidad global",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Stadium Layout - Norte
        Text(
            text = "Norte",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SectorCard(state.stadiumState.sectors[SectorName.NORTH])
        
        Spacer(modifier = Modifier.height(16.dp))

        // Este y Oeste
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Este",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SectorCard(state.stadiumState.sectors[SectorName.EAST])
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Oeste",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SectorCard(state.stadiumState.sectors[SectorName.WEST])
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Sur
        Text(
            text = "Sur",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SectorCard(state.stadiumState.sectors[SectorName.SOUTH])
        
        Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for nav bar
    }
}
