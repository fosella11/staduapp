package com.domingame.staduapp.feature.stadium.domain.repository

import com.domingame.staduapp.feature.stadium.domain.model.ConnectionState
import com.domingame.staduapp.feature.stadium.domain.model.EntryEvent
import kotlinx.coroutines.flow.Flow

interface StadiumRepository {
    fun entryEvents(): Flow<EntryEvent>
    fun connectionState(): Flow<ConnectionState>
    suspend fun connect()
    suspend fun disconnect()
}
