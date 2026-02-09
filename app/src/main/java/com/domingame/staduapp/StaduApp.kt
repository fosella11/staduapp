package com.domingame.staduapp

import android.app.Application
import com.domingame.staduapp.data.repository.StadiumRepositoryImpl
import com.domingame.staduapp.data.websocket.WebSocketManager
import com.domingame.staduapp.domain.engine.StadiumEngine
import com.domingame.staduapp.ui.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.Request

class StaduApp : Application() {

    private val okHttpClient by lazy { OkHttpClient() }
    
    // Default URL - can be overridden by user or build config
    // Requirement: "Cómo configurar URL (buildConfig / settings)" -> README will explain
    private val wsUrl = "ws://192.168.0.170:8765" // Android Emulator localhost
    
    private val wsRequest by lazy { 
        Request.Builder().url(wsUrl).build() 
    }
    
    val webSocketManager by lazy { WebSocketManager(okHttpClient, wsRequest) }
    
    val stadiumRepository by lazy { 
        StadiumRepositoryImpl(wsManager = webSocketManager)
    }
    
    val stadiumEngine by lazy { StadiumEngine() }
    
    val viewModelFactory by lazy { 
        MainViewModel.Factory(stadiumRepository, stadiumEngine) 
    }
}
