package com.domingame.staduapp.feature.stadium.presentation.model

import com.domingame.staduapp.feature.stadium.domain.model.ConnectionState
import com.domingame.staduapp.feature.stadium.domain.model.EntryEvent
import com.domingame.staduapp.feature.stadium.domain.model.GlobalMetrics
import com.domingame.staduapp.feature.stadium.domain.model.ProcessedEvent
import com.domingame.staduapp.feature.stadium.domain.model.StadiumState

data class StadiumUiState(
    val stadiumState: StadiumState? = null,
    val isLoading: Boolean = true,
    val events: List<ProcessedEvent> = emptyList() // Last events for log
)

// Reusing ConnectionState from Domain might be fine, or map to UI specific state
// Domain ConnectionState (CONNECTED, DISCONNECTED, ERROR, CONNECTING) is good enough for UI.
// But prompt asks for ConnectionUiState.
data class ConnectionUiState(
    val state: ConnectionState = ConnectionState.DISCONNECTED,
    val isConnecting: Boolean = false,
    val error: String? = null
)
