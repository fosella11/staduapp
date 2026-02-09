package com.domingame.staduapp.feature.stadium.domain

import com.domingame.staduapp.feature.stadium.domain.model.BlockName
import com.domingame.staduapp.feature.stadium.domain.model.SectorName

object StaduConfig {
    // Capacity per block
    const val BLOCK_CAPACITY = 20 // Default small capacity
    const val BLOCK_LOCK_THRESHOLD = 0.7f // 70%

    // Distances
    const val DISTANCE_INTRA_SECTOR = 10
    const val DISTANCE_ADJACENT_SECTOR = 50
    const val DISTANCE_OPPOSITE_SECTOR = 100

    // Map Gate String to Sector
    fun getSectorForGate(gate: String): SectorName {
        val g = gate.uppercase().trim()
        return when {
            g.contains("NORTE") || g.contains("NORTH") || g.endsWith("A") -> SectorName.NORTH
            g.contains("SUR") || g.contains("SOUTH") || g.endsWith("B") -> SectorName.SOUTH
            g.contains("ESTE") || g.contains("EAST") || g.endsWith("C") -> SectorName.EAST
            g.contains("OESTE") || g.contains("WEST") || g.endsWith("D") -> SectorName.WEST
            else -> SectorName.NORTH // Fallback
        }
    }

    // Priorities within a sector: C -> B -> A
    val BLOCK_PRIORITY = listOf(BlockName.C, BlockName.B, BlockName.A)

    val ADJACENT_SECTORS = mapOf(
        SectorName.NORTH to listOf(SectorName.EAST, SectorName.WEST),
        SectorName.SOUTH to listOf(SectorName.EAST, SectorName.WEST),
        SectorName.EAST to listOf(SectorName.NORTH, SectorName.SOUTH),
        SectorName.WEST to listOf(SectorName.NORTH, SectorName.SOUTH)
    )
}
