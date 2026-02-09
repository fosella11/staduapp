package com.domingame.staduapp.feature.stadium.domain.usecase

import com.domingame.staduapp.feature.stadium.domain.engine.StadiumEngine
import com.domingame.staduapp.feature.stadium.domain.model.StadiumState
import kotlinx.coroutines.flow.StateFlow

class ObserveStadiumStateUseCase(
    private val stadiumEngine: StadiumEngine
) {
    operator fun invoke(): StateFlow<StadiumState> = stadiumEngine.stadiumState
}
