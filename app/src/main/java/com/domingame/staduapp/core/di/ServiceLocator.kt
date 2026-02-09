package com.domingame.staduapp.core.di

import com.domingame.staduapp.feature.stadium.data.datasource.StadiumRemoteDataSource
import com.domingame.staduapp.feature.stadium.data.datasource.StadiumRemoteDataSourceImpl
import com.domingame.staduapp.feature.stadium.data.repository.StadiumRepositoryImpl
import com.domingame.staduapp.feature.stadium.domain.engine.StadiumEngine
import com.domingame.staduapp.feature.stadium.domain.repository.StadiumRepository
import com.domingame.staduapp.feature.stadium.domain.usecase.ConnectToStadiumUseCase
import com.domingame.staduapp.feature.stadium.domain.usecase.DisconnectFromStadiumUseCase
import com.domingame.staduapp.feature.stadium.domain.usecase.ObserveConnectionStateUseCase
import com.domingame.staduapp.feature.stadium.domain.usecase.ObserveEntryEventsUseCase
import com.domingame.staduapp.feature.stadium.domain.usecase.ObserveStadiumStateUseCase
import com.domingame.staduapp.feature.stadium.domain.usecase.ProcessEntryEventUseCase
import com.domingame.staduapp.feature.stadium.presentation.MainViewModel
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object ServiceLocator {

    private const val WS_URL = "ws://192.168.0.170:8765" // Should be configurable

    private val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS) // WebSocket keep-alive
            .build()
    }

    private val stadiumRemoteDataSource: StadiumRemoteDataSource by lazy {
        StadiumRemoteDataSourceImpl(okHttpClient, json)
    }

    private val stadiumRepository: StadiumRepository by lazy {
        StadiumRepositoryImpl(stadiumRemoteDataSource, WS_URL)
    }

    private val stadiumEngine: StadiumEngine by lazy {
        StadiumEngine()
    }

    // UseCases
    private val observeStadiumStateUseCase: ObserveStadiumStateUseCase by lazy {
        ObserveStadiumStateUseCase(stadiumEngine)
    }

    private val observeEntryEventsUseCase: ObserveEntryEventsUseCase by lazy {
        ObserveEntryEventsUseCase(stadiumRepository)
    }

    private val observeConnectionStateUseCase: ObserveConnectionStateUseCase by lazy {
        ObserveConnectionStateUseCase(stadiumRepository)
    }

    private val processEntryEventUseCase: ProcessEntryEventUseCase by lazy {
        ProcessEntryEventUseCase(stadiumEngine)
    }

    private val connectToStadiumUseCase: ConnectToStadiumUseCase by lazy {
        ConnectToStadiumUseCase(stadiumRepository)
    }

    private val disconnectFromStadiumUseCase: DisconnectFromStadiumUseCase by lazy {
        DisconnectFromStadiumUseCase(stadiumRepository)
    }

    val mainViewModelFactory: MainViewModel.Factory by lazy {
        MainViewModel.Factory(
            observeStadiumStateUseCase,
            observeEntryEventsUseCase,
            observeConnectionStateUseCase,
            processEntryEventUseCase,
            connectToStadiumUseCase,
            disconnectFromStadiumUseCase
        )
    }
}
