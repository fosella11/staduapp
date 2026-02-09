package com.domingame.staduapp.domain.engine

import com.domingame.staduapp.domain.StaduConfig
import com.domingame.staduapp.domain.model.AssignmentResult
import com.domingame.staduapp.domain.model.BlockName
import com.domingame.staduapp.domain.model.BlockState
import com.domingame.staduapp.domain.model.EntryEvent
import com.domingame.staduapp.domain.model.SectorName
import com.domingame.staduapp.domain.model.SectorState
import com.domingame.staduapp.domain.model.StadiumState

object AssignmentStrategy {

    fun determineAssignment(
        state: StadiumState,
        event: EntryEvent
    ): AssignmentResult {
        // 1. Check Multicolor
        if (event.shirtColor.equals("MULTICOLOR", ignoreCase = true)) {
            return AssignmentResult.Blocked("Multicolor shirt not allowed")
        }

        // 2. Check Blue -> North // TODO: Check if Gate is relevant for Blue? No, forces North.
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
        // Rule: "al bloque abierto más cercano dentro de ese sector" -> Standard priority C->B->A
        val northBlock = findBestBlockInSector(northSector)
        if (northBlock != null) {
            // Distance logic: "distancia_dentro_del_sector"
            val dist = StaduConfig.DISTANCE_INTRA_SECTOR
            return AssignmentResult.Success(SectorName.NORTH, northBlock.name, dist)
        }

        // Fallback: "fallback controlado al Bloque C más cercano disponible en cualquier otro sector"
        // Order: Adjacent (East/West) -> Opposite (South)
        // Distance calculation needs to reflect this.
        val fallbackOrder = listOf(SectorName.EAST, SectorName.WEST, SectorName.SOUTH)

        for (sectorName in fallbackOrder) {
            val sector = state.sectors[sectorName] ?: continue
            val blockC = sector.blocks[BlockName.C]

            // Check if Block C is valid
            if (blockC != null && !blockC.isFull && !blockC.isBlocked) {
                val baseDist =
                    if (sectorName == SectorName.SOUTH) StaduConfig.DISTANCE_OPPOSITE_SECTOR else StaduConfig.DISTANCE_ADJACENT_SECTOR
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
        // "asignar al bloque válido más cercano en un sector adyacente"
        val adjacentNames = StaduConfig.ADJACENT_SECTORS[targetSectorName] ?: emptyList()

        // We need to find the best among adjacent.
        // Both adjacent are distance 50 away. So pick the first available?
        // Or check both and pick best block?
        // Since priority is C->B->A, we can check adjacent 1 then adjacent 2.
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
            // "Bloqueo automático 70%: un bloque se bloquea... bloqueado debe descartarse"
            if (block != null && !block.isFull && !block.isBlocked) {
                return block
            }
        }
        return null
    }
}
