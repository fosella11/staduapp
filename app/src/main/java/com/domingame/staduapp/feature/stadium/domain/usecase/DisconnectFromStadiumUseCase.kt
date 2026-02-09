package com.domingame.staduapp.feature.stadium.domain.usecase

import com.domingame.staduapp.feature.stadium.domain.repository.StadiumRepository

class DisconnectFromStadiumUseCase(
    private val repository: StadiumRepository
) {
    suspend operator fun invoke() {
        repository.disconnect()
    }
}
