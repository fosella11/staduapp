package com.domingame.staduapp.feature.stadium.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EntryEvent(
    val type: String, // usually "ENTRY"
    val gate: String, // e.g. "Gate 1", "North Gate"
    val shirtColor: String
)
