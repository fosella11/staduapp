package com.domingame.staduapp.feature.stadium.data.datasource

import com.domingame.staduapp.feature.stadium.domain.model.ConnectionState
import com.domingame.staduapp.feature.stadium.domain.model.EntryEvent
import kotlinx.coroutines.flow.Flow

interface StadiumRemoteDataSource {
    fun observeEntryEvents(): Flow<EntryEvent>
    fun observeConnectionState(): Flow<ConnectionState>
    suspend fun connect(url: String)
    suspend fun disconnect()
}
