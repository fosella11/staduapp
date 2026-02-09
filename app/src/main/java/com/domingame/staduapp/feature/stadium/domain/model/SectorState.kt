package com.domingame.staduapp.feature.stadium.domain.model

data class SectorState(
    val name: SectorName,
    val blocks: Map<BlockName, BlockState>
) {
    val totalOccupants: Int get() = blocks.values.sumOf { it.occupants }
    val totalCapacity: Int get() = blocks.values.sumOf { it.capacity }
    val occupancyPercentage: Float get() = if (totalCapacity > 0) totalOccupants.toFloat() / totalCapacity else 0f
}
