package com.domingame.staduapp.feature.stadium.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.domingame.staduapp.feature.stadium.domain.model.AssignmentResult
import com.domingame.staduapp.feature.stadium.domain.model.ProcessedEvent
import com.domingame.staduapp.ui.theme.BlockedRed
import com.domingame.staduapp.ui.theme.OccupiedGreen
import com.domingame.staduapp.ui.theme.WarningOrange
import com.domingame.staduapp.ui.theme.PurpleMain
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogEventItem(processed: ProcessedEvent) {
    val result = processed.result
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(processed.timestamp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Status Icon, Event ID, Gate, Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (result is AssignmentResult.Blocked) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (result is AssignmentResult.Blocked) BlockedRed else OccupiedGreen,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "event-${processed.id.take(4)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                
                // Gate Capsule
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Puerta ${processed.originalEvent.gate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Middle Row: Ticket Color Chip and Status Text
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TicketColorChip(processed.originalEvent.shirtColor)
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = when (result) {
                        is AssignmentResult.Success -> "Asignado"
                        is AssignmentResult.Blocked -> "Bloqueado"
                        else -> "Rechazado"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Bottom Row: Details or Warning
            when (result) {
                is AssignmentResult.Success -> {
                    Text(
                        text = "Sector: ${result.sector.name} | Bloque: ${result.block}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                is AssignmentResult.Blocked -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Acceso bloqueado",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = BlockedRed
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun TicketColorChip(colorLabel: String) {
    val bgColor = when (colorLabel.uppercase()) {
        "BLACK" -> Color.Black
        "MULTICOLOR" -> PurpleMain // Or a gradient if possible, but PurpleMain is a good fallback
        "RED" -> BlockedRed
        "YELLOW" -> WarningOrange
        else -> Color.Gray
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = colorLabel.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            ),
            color = Color.White
        )
    }
}
