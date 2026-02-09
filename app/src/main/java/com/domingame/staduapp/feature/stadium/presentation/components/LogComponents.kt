package com.domingame.staduapp.feature.stadium.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.domingame.staduapp.feature.stadium.domain.model.AssignmentResult
import com.domingame.staduapp.feature.stadium.domain.model.ProcessedEvent
import com.domingame.staduapp.ui.theme.BlockedRed
import com.domingame.staduapp.ui.theme.OccupiedGreen
import com.domingame.staduapp.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogEventItem(processed: ProcessedEvent) {
    val result = processed.result
    
    val (statusColor, statusBgColor, statusText, statusIcon) = when (result) {
        is AssignmentResult.Success -> {
            val assignmentText = "Asignado: ${result.sector.name} - Bloque ${result.block}"
            Tuple4(OccupiedGreen, OccupiedGreen.copy(alpha = 0.15f), assignmentText, Icons.Filled.CheckCircle)
        }
        is AssignmentResult.Rejected -> {
            Tuple4(WarningOrange, WarningOrange.copy(alpha = 0.15f), "Rechazado", Icons.Filled.Close)
        }
        is AssignmentResult.Blocked -> {
            Tuple4(BlockedRed, BlockedRed.copy(alpha = 0.15f), "Acceso bloqueado", Icons.Filled.Lock)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(statusBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Event Details
            Column(Modifier.weight(1f)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "event ${processed.id.take(4)}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(processed.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Status Badge
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    color = statusColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusBgColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                
                // Additional Info
                if (result is AssignmentResult.Success) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Puerta ${processed.originalEvent.gate} • ${processed.originalEvent.shirtColor}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Helper data class for tuple
private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
