package com.domingame.staduapp.feature.stadium.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.domingame.staduapp.feature.stadium.domain.model.BlockName
import com.domingame.staduapp.feature.stadium.domain.model.BlockState
import com.domingame.staduapp.feature.stadium.domain.model.SectorState
import com.domingame.staduapp.ui.theme.BlockedRed
import com.domingame.staduapp.ui.theme.OccupiedGreen
import com.domingame.staduapp.ui.theme.PurpleMain
import com.domingame.staduapp.ui.theme.WarningOrange

@Composable
fun ResponsiveSectorGrid(sector: SectorState?) {
    if (sector == null) return
    
    // Single row with 3 blocks: A, B, C
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GrowingBlockItem(
            block = sector.blocks[BlockName.A],
            blockLabel = "A",
            modifier = Modifier.weight(1f)
        )
        GrowingBlockItem(
            block = sector.blocks[BlockName.B],
            blockLabel = "B",
            modifier = Modifier.weight(1f)
        )
        GrowingBlockItem(
            block = sector.blocks[BlockName.C],
            blockLabel = "C",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun GrowingBlockItem(
    block: BlockState?,
    blockLabel: String,
    modifier: Modifier = Modifier
) {
    if (block == null) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF1EFF3))
        )
        return
    }
    
    val fillPercentage = block.occupancyPercentage.coerceIn(0f, 1f)
    
    val color = when {
        block.isBlocked -> BlockedRed
        fillPercentage >= 0.5f -> WarningOrange
        else -> PurpleMain
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF1EFF3)),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Fill background (grows from bottom)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fillPercentage)
                    .background(color.copy(alpha = 0.6f))
            )
            
            // Content
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (block.isBlocked) {
                    Text(
                        text = "70%\nBLOQUEADO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            lineHeight = 10.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = Color.White,
                        modifier = Modifier
                            .background(BlockedRed, RoundedCornerShape(4.dp))
                            .padding(4.dp)
                    )
                } else if (block.occupants > 0) {
                   Text(
                        text = "${block.occupants}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (fillPercentage > 0.4f) Color.White else Color.Black
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = blockLabel,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Keep old components for backward compatibility
@Composable
fun SectorCard(sector: SectorState?) {
    ResponsiveSectorGrid(sector)
}

@Composable
fun BlockItem(block: BlockState?) {
    // Deprecated - use GrowingBlockItem instead
}
