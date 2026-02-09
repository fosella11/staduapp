package com.domingame.staduapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.domingame.staduapp.domain.engine.StadiumEngine
import com.domingame.staduapp.domain.model.ProcessedEvent
import com.domingame.staduapp.domain.repository.StadiumRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: StadiumRepository,
    private val engine: StadiumEngine
) : ViewModel() {

    // Connection State
    val connectionState = repository.connectionState

    // Stadium State (Sectors, Blocks, Metrics)
    val stadiumState = engine.stadiumState

    // Event Log (Limited to last 500 events)
    private val _eventLog = MutableStateFlow<List<ProcessedEvent>>(emptyList())
    val eventLog = _eventLog.asStateFlow()

    init {
        startProcessing()
    }

    private fun startProcessing() {
        viewModelScope.launch {
            repository.getEvents()
                .collect { event ->
                    // Process event through engine (thread-safe, updates state)
                    val processed = engine.processEvent(event)
                    
                    // Add to log
                    _eventLog.update { current ->
                        val newList = listOf(processed) + current
                        if (newList.size > 500) newList.take(500) else newList
                    }
                }
        }
    }
    
    // Factory for manual DI
    class Factory(
        private val repository: StadiumRepository,
        private val engine: StadiumEngine
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository, engine) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
