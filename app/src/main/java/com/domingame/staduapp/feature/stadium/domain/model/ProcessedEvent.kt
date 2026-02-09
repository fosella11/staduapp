package com.domingame.staduapp.feature.stadium.domain.model

data class ProcessedEvent(
    val id: String, // UUID
    val originalEvent: EntryEvent,
    val result: AssignmentResult,
    val timestamp: Long = System.currentTimeMillis()
)
