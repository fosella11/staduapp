package com.domingame.staduapp.feature.stadium.domain.usecase

import com.domingame.staduapp.feature.stadium.domain.engine.StadiumEngine
import com.domingame.staduapp.feature.stadium.domain.model.EntryEvent
import com.domingame.staduapp.feature.stadium.domain.model.ProcessedEvent

class ProcessEntryEventUseCase(
    private val stadiumEngine: StadiumEngine
) {
    suspend operator fun invoke(event: EntryEvent): ProcessedEvent {
        return stadiumEngine.processEvent(event)
    }
}
