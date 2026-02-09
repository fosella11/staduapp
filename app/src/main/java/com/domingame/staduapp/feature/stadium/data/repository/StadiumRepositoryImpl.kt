package com.domingame.staduapp.feature.stadium.data.repository

import com.domingame.staduapp.feature.stadium.data.datasource.StadiumRemoteDataSource
import com.domingame.staduapp.feature.stadium.domain.model.ConnectionState
import com.domingame.staduapp.feature.stadium.domain.model.EntryEvent
import com.domingame.staduapp.feature.stadium.domain.repository.StadiumRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.pow

class StadiumRepositoryImpl(
    private val remoteDataSource: StadiumRemoteDataSource,
    private val wsUrl: String, // Injected via DI
    private val externalScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : StadiumRepository {

    private var connectionJob: Job? = null

    // Backoff configuration
    private val initialBackoff = 1000L
    private val maxBackoff = 30000L
    private val backoffFactor = 2.0

    override fun entryEvents(): Flow<EntryEvent> = remoteDataSource.observeEntryEvents()

    override fun connectionState(): Flow<ConnectionState> =
        remoteDataSource.observeConnectionState()

    override suspend fun connect() {
        if (connectionJob?.isActive == true) return

        connectionJob = externalScope.launch {
            // Initial connect attempt
            remoteDataSource.connect(wsUrl)

            var retryAttempt = 0

            // Monitor state to trigger reconnections
            remoteDataSource.observeConnectionState().collectLatest { state ->
                when (state) {
                    ConnectionState.CONNECTED -> {
                        retryAttempt = 0
                    }

                    ConnectionState.ERROR, ConnectionState.DISCONNECTED -> {
                        // Only retry if we haven't explicitly disconnected (i.e. connectionJob is active)
                        // But here inside launch, we are active.
                        // Wait, if disconnect() cancels this job, then we won't reach here?
                        // disconnect() cancels connectionJob. So this block shouldn't run if user disconnected.

                        val backoff = (initialBackoff * backoffFactor.pow(retryAttempt)).toLong()
                        val delayTime = min(backoff, maxBackoff)

                        delay(delayTime)
                        remoteDataSource.connect(wsUrl)
                        retryAttempt++
                    }

                    else -> {} // CONNECTING, etc.
                }
            }
        }
    }

    override suspend fun disconnect() {
        connectionJob?.cancel()
        connectionJob = null
        remoteDataSource.disconnect()
    }
}
