package com.domingame.staduapp.feature.stadium.domain.engine

import com.domingame.staduapp.feature.stadium.domain.StaduConfig
import com.domingame.staduapp.feature.stadium.domain.model.AssignmentResult
import com.domingame.staduapp.feature.stadium.domain.model.BlockName
import com.domingame.staduapp.feature.stadium.domain.model.BlockState
import com.domingame.staduapp.feature.stadium.domain.model.EntryEvent
import com.domingame.staduapp.feature.stadium.domain.model.GlobalMetrics
import com.domingame.staduapp.feature.stadium.domain.model.ProcessedEvent
import com.domingame.staduapp.feature.stadium.domain.model.SectorName
import com.domingame.staduapp.feature.stadium.domain.model.SectorState
import com.domingame.staduapp.feature.stadium.domain.model.StadiumState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class StadiumEngine {

    private val _stadiumState = MutableStateFlow(createInitialState())
    val stadiumState: StateFlow<StadiumState> = _stadiumState.asStateFlow()

    private val mutex = Mutex()

    // Process event sequentially to guarantee consistency
    suspend fun processEvent(event: EntryEvent): ProcessedEvent {
        return mutex.withLock {
            val currentState = _stadiumState.value

            // Calculate assignment based on current state
            val result = AssignmentStrategy.determineAssignment(currentState, event)

            // Calculate new immutable state
            val newState = applyResult(currentState, result)

            // Atomically update state
            _stadiumState.value = newState

            ProcessedEvent(UUID.randomUUID().toString(), event, result)
        }
    }

    private fun applyResult(currentState: StadiumState, result: AssignmentResult): StadiumState {
        // Handle Rejected/Blocked cases first (simpler update)
        if (result !is AssignmentResult.Success) {
            val newMetrics = when (result) {
                is AssignmentResult.Blocked -> currentState.metrics.copy(
                    totalBlocked = currentState.metrics.totalBlocked + 1
                )

                is AssignmentResult.Rejected -> currentState.metrics.copy(
                    totalRefused = currentState.metrics.totalRefused + 1
                )

                else -> currentState.metrics // Should not happen given logic
            }
            return currentState.copy(metrics = newMetrics)
        }

        // Handle Success: Update Block -> Sector -> Stadium
        val sector = currentState.sectors[result.sector] ?: return currentState
        val block = sector.blocks[result.block] ?: return currentState

        // Calculate new block stats
        val newOccupants = block.occupants + 1
        // "Bloqueo automático 70%"
        val isNowBlocked = newOccupants >= (block.capacity * StaduConfig.BLOCK_LOCK_THRESHOLD)

        val newBlock = block.copy(
            occupants = newOccupants,
            accumulatedDistance = block.accumulatedDistance + result.distance,
            assignmentCount = block.assignmentCount + 1,
            isBlocked = isNowBlocked
        )

        // Propagate changes up the immutable tree
        val newBlocksMap = sector.blocks + (result.block to newBlock)
        val newSector = sector.copy(blocks = newBlocksMap)
        val newSectorsMap = currentState.sectors + (result.sector to newSector)

        // Update Global Metrics incrementally
        val oldTotalAdmitted = currentState.metrics.totalAdmitted
        val oldGlobalAvg = currentState.metrics.averageDistanceGlobal

        // Avoid division by zero
        val oldTotalDist = oldGlobalAvg * oldTotalAdmitted
        val newTotalDist = oldTotalDist + result.distance
        val newTotalAdmitted = oldTotalAdmitted + 1
        val newGlobalAvg = newTotalDist / newTotalAdmitted

        val newMetrics = currentState.metrics.copy(
            totalAdmitted = newTotalAdmitted,
            averageDistanceGlobal = newGlobalAvg
        )

        return currentState.copy(sectors = newSectorsMap, metrics = newMetrics)
    }

    private fun createInitialState(): StadiumState {
        val sectors = SectorName.values().associateWith { sectorName ->
            val blocks = BlockName.values().associateWith { blockName ->
                BlockState(name = blockName, capacity = StaduConfig.BLOCK_CAPACITY)
            }
            SectorState(name = sectorName, blocks = blocks)
        }
        return StadiumState(sectors = sectors, metrics = GlobalMetrics())
    }
}
