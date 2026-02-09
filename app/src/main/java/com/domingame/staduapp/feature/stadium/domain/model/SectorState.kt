package com.domingame.staduapp.feature.stadium.domain.model

data class SectorState(
    val name: SectorName,
    val blocks: Map<BlockName, BlockState>
) {
    val totalCapacity: Int get() = blocks.values.sumOf { it.capacity }
}
