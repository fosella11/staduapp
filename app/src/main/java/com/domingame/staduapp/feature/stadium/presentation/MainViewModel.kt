package com.domingame.staduapp.feature.stadium.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.domingame.staduapp.feature.stadium.domain.model.ConnectionState
import com.domingame.staduapp.feature.stadium.domain.usecase.ConnectToStadiumUseCase
import com.domingame.staduapp.feature.stadium.domain.usecase.DisconnectFromStadiumUseCase
import com.domingame.staduapp.feature.stadium.domain.usecase.ObserveConnectionStateUseCase
import com.domingame.staduapp.feature.stadium.domain.usecase.ObserveEntryEventsUseCase
import com.domingame.staduapp.feature.stadium.domain.usecase.ObserveStadiumStateUseCase
import com.domingame.staduapp.feature.stadium.domain.usecase.ProcessEntryEventUseCase
import com.domingame.staduapp.feature.stadium.presentation.model.ConnectionUiState
import com.domingame.staduapp.feature.stadium.presentation.model.StadiumUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val observeStadiumState: ObserveStadiumStateUseCase,
    private val observeEntryEvents: ObserveEntryEventsUseCase,
    private val observeConnectionState: ObserveConnectionStateUseCase,
    private val processEntryEvent: ProcessEntryEventUseCase,
    private val connectToStadium: ConnectToStadiumUseCase,
    private val disconnectFromStadium: DisconnectFromStadiumUseCase
) : ViewModel() {

    // Internal mutable state for Stadium Logic
    private val _stadiumUiState = MutableStateFlow(StadiumUiState())
    val stadiumUiState: StateFlow<StadiumUiState> = _stadiumUiState.asStateFlow()

    // Connection State mapping to UI
    val connectionUiState: StateFlow<ConnectionUiState> = observeConnectionState()
        .map { state ->
            ConnectionUiState(
                state = state,
                isConnecting = state == ConnectionState.CONNECTING,
                error = if (state == ConnectionState.ERROR) "Connection Error" else null
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConnectionUiState()
        )

    init {
        // Observe Domain State and Update UI State
        observeStadiumState()
            .onEach { domainState ->
                _stadiumUiState.update { it.copy(stadiumState = domainState, isLoading = false) }
            }
            .launchIn(viewModelScope)

        // Process Incoming Events
        // The event processing updates the engine state, which then updates uiState via above flow.
        // We also need to add ProcessedEvent to the log list in UI State.
        observeEntryEvents()
            .onEach { event ->
                val processed = processEntryEvent(event)
                _stadiumUiState.update { currentState ->
                    val newEvents = (listOf(processed) + currentState.events).take(500)
                    currentState.copy(events = newEvents)
                }
            }
            .launchIn(viewModelScope)
    }

    fun startConnection() {
        viewModelScope.launch {
            connectToStadium()
        }
    }

    fun stopConnection() {
        viewModelScope.launch {
            disconnectFromStadium()
        }
    }

    class Factory(
        private val observeStadiumState: ObserveStadiumStateUseCase,
        private val observeEntryEvents: ObserveEntryEventsUseCase,
        private val observeConnectionState: ObserveConnectionStateUseCase,
        private val processEntryEvent: ProcessEntryEventUseCase,
        private val connectToStadium: ConnectToStadiumUseCase,
        private val disconnectFromStadium: DisconnectFromStadiumUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(
                observeStadiumState,
                observeEntryEvents,
                observeConnectionState,
                processEntryEvent,
                connectToStadium,
                disconnectFromStadium
            ) as T
        }
    }
}
