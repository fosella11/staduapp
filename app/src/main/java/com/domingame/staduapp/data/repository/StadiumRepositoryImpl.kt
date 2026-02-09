package com.domingame.staduapp.data.repository

import com.domingame.staduapp.data.websocket.WebSocketEvent
import com.domingame.staduapp.data.websocket.WebSocketManager
import com.domingame.staduapp.domain.model.EntryEvent
import com.domingame.staduapp.domain.repository.ConnectionState
import com.domingame.staduapp.domain.repository.StadiumRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import java.io.IOException

class StadiumRepositoryImpl(
    private val wsManager: WebSocketManager,
    private val json: Json = Json { ignoreUnknownKeys = true, isLenient = true}
) : StadiumRepository {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    override fun getEvents(): Flow<EntryEvent> {
        return wsManager.connect()
            .onStart { _connectionState.value = ConnectionState.CONNECTING }
            .onEach { event ->
                when (event) {
                    is WebSocketEvent.Connected -> {
                        _connectionState.value = ConnectionState.CONNECTED
                    }
                    is WebSocketEvent.Disconnected -> {
                        _connectionState.value = ConnectionState.DISCONNECTED
                    }
                    is WebSocketEvent.Error -> {
                        _connectionState.value = ConnectionState.ERROR
                    }
                    is WebSocketEvent.Message -> {
                        // Handled in transform
                    }
                }
            }
            .transform { event ->
                if (event is WebSocketEvent.Message) {
                    try {
                        val parsed = json.decodeFromString<EntryEvent>(event.text)
                        emit(parsed)
                    } catch (e: Exception) {
                        println("Parsing error: ${e.message} for ${event.text}")
                    }
                } else if (event is WebSocketEvent.Error) {
                     // Propagate error to trigger retry
                     throw IOException(event.t)
                }
            }
            .retryWhen { cause, attempt ->
                _connectionState.value = ConnectionState.RECONNECTING
                val delayTime = (attempt + 1) * 1000L
                delay(if (delayTime > 5000L) 5000L else delayTime)
                true // Always retry
            }
            .catch { 
                _connectionState.value = ConnectionState.ERROR
                // Could emit a special error event or just stop
            }
    }

    override suspend fun connect() {
        // Managed by flow collection
    }

    override suspend fun disconnect() {
        // Managed by flow cancellation
    }
}
