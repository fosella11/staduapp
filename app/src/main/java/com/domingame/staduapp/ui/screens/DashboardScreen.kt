package com.domingame.staduapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domingame.staduapp.domain.model.BlockName
import com.domingame.staduapp.domain.model.BlockState
import com.domingame.staduapp.domain.model.SectorName
import com.domingame.staduapp.domain.model.SectorState
import com.domingame.staduapp.ui.MainViewModel
import com.domingame.staduapp.ui.theme.BlockedRed
import com.domingame.staduapp.ui.theme.OccupiedGreen
import com.domingame.staduapp.ui.theme.WarningOrange

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val state by viewModel.stadiumState.collectAsState()
    
    // Calculate global percentage for UI
    val totalCapacity = state.sectors.values.sumOf { it.totalCapacity }
    val globalPercentage = if (totalCapacity > 0) state.metrics.totalAdmitted.toFloat() / totalCapacity else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Global Capacity
        Text(
            text = "Total Admitted: ${state.metrics.totalAdmitted}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { globalPercentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Text(
            text = "Global Occupancy: ${(globalPercentage * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Grid Layout matching Figma (roughly)
        // North
        SectorCard(state.sectors[SectorName.NORTH])

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(Modifier.weight(1f).padding(end = 6.dp)) {
                SectorCard(state.sectors[SectorName.WEST])
            }
            Box(Modifier.weight(1f).padding(start = 6.dp)) {
                SectorCard(state.sectors[SectorName.EAST])
            }
        }

        // South
        SectorCard(state.sectors[SectorName.SOUTH])
    }
}

@Composable
fun SectorCard(sector: SectorState?) {
    if (sector == null) return
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = sector.name.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(sector.occupancyPercentage * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Blocks A, B, C
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(BlockName.A, BlockName.B, BlockName.C).forEach { name ->
                    BlockItem(sector.blocks[name])
                }
            }
        }
    }
}

@Composable
fun BlockItem(block: BlockState?) {
    if (block == null) return
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            val color = when {
                block.isBlocked -> BlockedRed
                block.isFull -> WarningOrange
                else -> OccupiedGreen // Or Primary
            }
            
            CircularProgressIndicator(
                progress = { block.occupancyPercentage },
                modifier = Modifier.size(50.dp),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 6.dp
            )
            Text(
                text = "${block.occupants}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(block.name.name, style = MaterialTheme.typography.labelMedium)
        
        if (block.isBlocked) {
             Text("BLOCKED", color = BlockedRed, style = MaterialTheme.typography.labelSmall)
        }
    }
}
