package com.domingame.staduapp.feature.stadium.domain.model

data class GlobalMetrics(
    val totalAdmitted: Int = 0,
    val totalRefused: Int = 0,
    val totalBlocked: Int = 0,
    val averageDistanceGlobal: Float = 0f
)
