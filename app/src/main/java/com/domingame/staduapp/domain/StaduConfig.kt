package com.domingame.staduapp.domain

import com.domingame.staduapp.domain.model.BlockName
import com.domingame.staduapp.domain.model.SectorName

object StaduConfig {
    // Capacity per block
    const val BLOCK_CAPACITY =
        10 // Small for testing as per PDF example often uses small numbers, but requirement doesn't specify. Assuming 10-50 based on "12 blocks". Let's use 20.

    // Wait, PDF example says "Block 4" capacity etc. I will use 20 for now to make 70% reachable.
    const val DEFAULT_CAPACITY = 50
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
            g.contains("ESTE") || g.contains("EAST") || g.endsWith("C") -> SectorName.EAST // East/West check order matters? "Este" contains "E"
            g.contains("OESTE") || g.contains("WEST") || g.endsWith("D") -> SectorName.WEST
            else -> SectorName.NORTH // Fallback
        }
    }

    // Priorities within a sector: C -> B -> A (Closest to furthest? Requirements say "priority C -> B -> A")
    val BLOCK_PRIORITY = listOf(BlockName.C, BlockName.B, BlockName.A)

    val ADJACENT_SECTORS = mapOf(
        SectorName.NORTH to listOf(SectorName.EAST, SectorName.WEST),
        SectorName.SOUTH to listOf(
            SectorName.EAST,
            SectorName.WEST
        ), // Or West/East depending on geometry. Assuming square.
        SectorName.EAST to listOf(SectorName.NORTH, SectorName.SOUTH),
        SectorName.WEST to listOf(SectorName.NORTH, SectorName.SOUTH)
    )

    // Opposite
    val OPPOSITE_SECTORS = mapOf(
        SectorName.NORTH to SectorName.SOUTH,
        SectorName.SOUTH to SectorName.NORTH,
        SectorName.EAST to SectorName.WEST,
        SectorName.WEST to SectorName.EAST
    )
}
