package com.domingame.staduapp.feature.stadium.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.domingame.staduapp.feature.stadium.domain.model.BlockName
import com.domingame.staduapp.feature.stadium.domain.model.BlockState
import com.domingame.staduapp.feature.stadium.domain.model.SectorState
import com.domingame.staduapp.ui.theme.BlockedRed
import com.domingame.staduapp.ui.theme.OccupiedGreen
import com.domingame.staduapp.ui.theme.WarningOrange

@Composable
fun SectorCard(sector: SectorState?) {
    if (sector == null) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Blocks Grid - 3 columns (A, B, C)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(BlockName.A, BlockName.B, BlockName.C).forEach { blockName ->
                    BlockItem(sector.blocks[blockName])
                }
            }
        }
    }
}

@Composable
fun BlockItem(block: BlockState?) {
    if (block == null) return
    
    val backgroundColor = when {
        block.isBlocked -> BlockedRed.copy(alpha = 0.15f)
        block.occupancyPercentage >= 0.5f -> WarningOrange.copy(alpha = 0.15f)
        block.occupants > 0 -> OccupiedGreen.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val textColor = when {
        block.isBlocked -> BlockedRed
        block.occupancyPercentage >= 0.5f -> WarningOrange
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        // Block Label
        Text(
            text = block.name.name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Occupancy Count
        Text(
            text = "${block.occupants}",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )
        
        // Status Badge
        if (block.isBlocked) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "70% BLOQUEADO",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = BlockedRed,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(BlockedRed.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
