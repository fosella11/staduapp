package com.domingame.staduapp.feature.stadium.domain.model

data class StadiumState(
    val sectors: Map<SectorName, SectorState>,
    val metrics: GlobalMetrics
)
