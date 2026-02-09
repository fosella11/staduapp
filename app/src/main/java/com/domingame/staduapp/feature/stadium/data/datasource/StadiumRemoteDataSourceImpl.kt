package com.domingame.staduapp.feature.stadium.data.datasource

import android.util.Log
import com.domingame.staduapp.feature.stadium.domain.model.ConnectionState
import com.domingame.staduapp.feature.stadium.domain.model.EntryEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class StadiumRemoteDataSourceImpl(
    private val client: OkHttpClient,
    private val json: Json
) : StadiumRemoteDataSource {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    private val _entryEvents = MutableSharedFlow<EntryEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private var webSocket: WebSocket? = null

    override fun observeEntryEvents(): Flow<EntryEvent> = _entryEvents.asSharedFlow()

    override fun observeConnectionState(): Flow<ConnectionState> = _connectionState.asStateFlow()

    override suspend fun connect(url: String) {
        // If already connected or connecting, do nothing
        if (_connectionState.value == ConnectionState.CONNECTED ||
            _connectionState.value == ConnectionState.CONNECTING
        ) return

        _connectionState.value = ConnectionState.CONNECTING

        try {
            val request = Request.Builder().url(url).build()
            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    _connectionState.value = ConnectionState.CONNECTED
                    Log.d("StadiumRemote", "WebSocket Connected")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    try {
                        val event = json.decodeFromString<EntryEvent>(text)
                        // Emit event (blocking tryEmit as buffer logic handles backpressure in flow collectors usually)
                        // For MutableSharedFlow with replay=0 and default BufferOverflow.SUSPEND, emit is safer but can suspend inside callback.
                        // using tryEmit if no one is listening is fine.
                        _entryEvents.tryEmit(event)
                    } catch (e: Exception) {
                        Log.e("StadiumRemote", "Error parsing event: ${e.message}")
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("StadiumRemote", "WebSocket Failure: ${t.message}")
                    _connectionState.value = ConnectionState.ERROR
                    // We don't nullify webSocket here immediately as OkHttp might retry internally? 
                    // No, OkHttp WebSocket doesn't auto-reconnect.
                    // We let Repository handle retry based on ERROR state.
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("StadiumRemote", "WebSocket Closed: $reason")
                    _connectionState.value = ConnectionState.DISCONNECTED
                }
            })
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.ERROR
        }
    }

    override suspend fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }
}
