package com.domingame.staduapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domingame.staduapp.domain.model.AssignmentResult
import com.domingame.staduapp.domain.model.ProcessedEvent
import com.domingame.staduapp.ui.MainViewModel
import com.domingame.staduapp.ui.theme.BlockedRed
import com.domingame.staduapp.ui.theme.OccupiedGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogScreen(viewModel: MainViewModel) {
    val events by viewModel.eventLog.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp), // Bottom padding for nav bar overlap if needed
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = events,
            key = { it.id }
        ) { processed ->
            LogEventItem(processed)
        }
    }
}

@Composable
fun LogEventItem(processed: ProcessedEvent) {
    val result = processed.result
    val (icon, color, statusText) = when (result) {
        is AssignmentResult.Success -> Triple(Icons.Filled.CheckCircle, OccupiedGreen, "Assigned: ${result.sector.name}-${result.block} (+${result.distance}m)")
        is AssignmentResult.Rejected -> Triple(Icons.Filled.Close, BlockedRed, "Refused: ${result.reason}")
        is AssignmentResult.Blocked -> Triple(Icons.Filled.Lock, Color.Black, "Blocked: ${result.reason}")
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(
                    Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gate ${processed.originalEvent.gate}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(processed.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                
                Text(
                    text = processed.originalEvent.shirtColor,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (result is AssignmentResult.Success) MaterialTheme.colorScheme.onSurface else color,
                    fontWeight = if (result !is AssignmentResult.Success) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
