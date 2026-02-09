package com.domingame.staduapp.feature.stadium.domain.usecase

import com.domingame.staduapp.feature.stadium.domain.model.EntryEvent
import com.domingame.staduapp.feature.stadium.domain.repository.StadiumRepository
import kotlinx.coroutines.flow.Flow

class ObserveEntryEventsUseCase(
    private val repository: StadiumRepository
) {
    operator fun invoke(): Flow<EntryEvent> = repository.entryEvents()
}
