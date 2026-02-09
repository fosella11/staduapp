package com.domingame.staduapp.domain.repository

import com.domingame.staduapp.domain.model.EntryEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

enum class ConnectionState {
    CONNECTING, CONNECTED, RECONNECTING, DISCONNECTED, ERROR
}

interface StadiumRepository {
    val connectionState: StateFlow<ConnectionState>
    fun getEvents(): Flow<EntryEvent>
    suspend fun connect()
    suspend fun disconnect()
}
