package com.domingame.staduapp.feature.stadium.domain.model

sealed interface AssignmentResult {
    data class Success(val sector: SectorName, val block: BlockName, val distance: Int) :
        AssignmentResult

    data class Rejected(val reason: String) : AssignmentResult // Capacity exceeded
    data class Blocked(val reason: String) : AssignmentResult // Multicolor or security block
}
