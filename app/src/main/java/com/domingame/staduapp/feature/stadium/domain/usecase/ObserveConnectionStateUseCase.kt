package com.domingame.staduapp.feature.stadium.domain.usecase

import com.domingame.staduapp.feature.stadium.domain.model.ConnectionState
import com.domingame.staduapp.feature.stadium.domain.repository.StadiumRepository
import kotlinx.coroutines.flow.Flow

class ObserveConnectionStateUseCase(
    private val repository: StadiumRepository
) {
    operator fun invoke(): Flow<ConnectionState> = repository.connectionState()
}
