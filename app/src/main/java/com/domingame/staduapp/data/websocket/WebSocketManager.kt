package com.domingame.staduapp.data.websocket

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager(
    private val client: OkHttpClient,
    private val request: Request
) {

    fun connect(): Flow<WebSocketEvent> = callbackFlow {
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                trySendBlocking(WebSocketEvent.Connected)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                trySendBlocking(WebSocketEvent.Message(text))
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                trySendBlocking(WebSocketEvent.Error(t))
                close(t) // Propagate error downstream to trigger retry
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                trySendBlocking(WebSocketEvent.Disconnected)
                close()
            }
        }

        val ws = client.newWebSocket(request, listener)
        awaitClose {
            ws.cancel()
        }
    }
}

sealed interface WebSocketEvent {
    data object Connected : WebSocketEvent
    data object Disconnected : WebSocketEvent // data object available since Kotlin 1.9
    data class Error(val t: Throwable) : WebSocketEvent
    data class Message(val text: String) : WebSocketEvent
}
