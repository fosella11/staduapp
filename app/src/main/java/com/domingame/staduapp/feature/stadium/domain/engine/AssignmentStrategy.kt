package com.domingame.staduapp.feature.stadium.domain.engine

import com.domingame.staduapp.feature.stadium.domain.StaduConfig
import com.domingame.staduapp.feature.stadium.domain.model.*

object AssignmentStrategy {

    fun determineAssignment(
        state: StadiumState,
        event: EntryEvent
    ): AssignmentResult {
        // 1. Check Multicolor
        if (event.shirtColor.equals("MULTICOLOR", ignoreCase = true)) {
            return AssignmentResult.Blocked("Multicolor shirt not allowed")
        }

        // 2. Check Blue -> North
        if (event.shirtColor.equals("BLUE", ignoreCase = true)) {
            return assignBlueShirt(state)
        }

        // 3. Standard
        return assignStandard(state, event.gate)
    }

    private fun assignBlueShirt(state: StadiumState): AssignmentResult {
        // Force North
        val northSector = state.sectors[SectorName.NORTH] 
            ?: return AssignmentResult.Rejected("Sector North not found")
        
        // Find best block in North
        val northBlock = findBestBlockInSector(northSector)
        if (northBlock != null) {
            val dist = StaduConfig.DISTANCE_INTRA_SECTOR
            return AssignmentResult.Success(SectorName.NORTH, northBlock.name, dist)
        }

        // Fallback: Block C in any other sector
        val fallbackOrder = listOf(SectorName.EAST, SectorName.WEST, SectorName.SOUTH)
        
        for (sectorName in fallbackOrder) {
            val sector = state.sectors[sectorName] ?: continue
            val blockC = sector.blocks[BlockName.C]
            
            // Check if Block C is valid
            if (blockC != null && !blockC.isFull && !blockC.isBlocked) {
                val baseDist = if (sectorName == SectorName.SOUTH) StaduConfig.DISTANCE_OPPOSITE_SECTOR else StaduConfig.DISTANCE_ADJACENT_SECTOR
                val totalDist = baseDist + StaduConfig.DISTANCE_INTRA_SECTOR
                return AssignmentResult.Success(sectorName, BlockName.C, totalDist)
            }
        }
        
        return AssignmentResult.Rejected("Blue shirt rejected: North blocked and no fallback Block C available")
    }

    private fun assignStandard(state: StadiumState, gate: String): AssignmentResult {
        val targetSectorName = StaduConfig.getSectorForGate(gate)
        val targetSector = state.sectors[targetSectorName] 
            ?: return AssignmentResult.Rejected("Invalid Gate mapping: $gate")

        // 1. Try Target Sector
        val bestBlock = findBestBlockInSector(targetSector)
        if (bestBlock != null) {
             val dist = StaduConfig.DISTANCE_INTRA_SECTOR
             return AssignmentResult.Success(targetSectorName, bestBlock.name, dist)
        }

        // 2. Saturation: Try Adjacent Sectors
        val adjacentNames = StaduConfig.ADJACENT_SECTORS[targetSectorName] ?: emptyList()
        
        for (adjName in adjacentNames) {
            val adjSector = state.sectors[adjName] ?: continue
            val adjBlock = findBestBlockInSector(adjSector)
            if (adjBlock != null) {
                val dist = StaduConfig.DISTANCE_ADJACENT_SECTOR + StaduConfig.DISTANCE_INTRA_SECTOR
                return AssignmentResult.Success(adjName, adjBlock.name, dist)
            }
        }

        return AssignmentResult.Rejected("Sector $targetSectorName and adjacent sectors saturated")
    }

    private fun findBestBlockInSector(sector: SectorState): BlockState? {
        // Priority C -> B -> A defined in Config
        for (blockName in StaduConfig.BLOCK_PRIORITY) { // C, B, A
            val block = sector.blocks[blockName]
            if (block != null && !block.isFull && !block.isBlocked) {
                return block
            }
        }
        return null
    }
}
