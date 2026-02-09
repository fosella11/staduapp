package com.domingame.staduapp.domain.model

import kotlinx.serialization.Serializable

enum class SectorName { NORTH, SOUTH, EAST, WEST }
enum class BlockName { A, B, C }
enum class ShirtColor { BLUE, RED, GREEN, YELLOW, BLACK, MULTICOLOR, OTHER }

@Serializable
data class EntryEvent(
    val type: String, // usually "ENTRY"
    val gate: String, // e.g. "Gate 1", "North Gate"
    val shirtColor: String
)

data class BlockState(
    val name: BlockName,
    val capacity: Int = 20, // Default configurable
    val occupants: Int = 0,
    val accumulatedDistance: Int = 0,
    val assignmentCount: Int = 0,
    val isBlocked: Boolean = false // True if occupants >= 70% capacity
) {
    val occupancyPercentage: Float get() = if (capacity > 0) occupants.toFloat() / capacity else 0f
    val isFull: Boolean get() = occupants >= capacity
    val averageDistance: Float get() = if (assignmentCount > 0) accumulatedDistance.toFloat() / assignmentCount else 0f
}

data class SectorState(
    val name: SectorName,
    val blocks: Map<BlockName, BlockState>
) {
    val totalOccupants: Int get() = blocks.values.sumOf { it.occupants }
    val totalCapacity: Int get() = blocks.values.sumOf { it.capacity }
    val occupancyPercentage: Float get() = if (totalCapacity > 0) totalOccupants.toFloat() / totalCapacity else 0f
}

data class GlobalMetrics(
    val totalAdmitted: Int = 0,
    val totalRefused: Int = 0,
    val totalBlocked: Int = 0,
    val averageDistanceGlobal: Float = 0f
)

sealed interface AssignmentResult {
    data class Success(val sector: SectorName, val block: BlockName, val distance: Int) : AssignmentResult
    data class Rejected(val reason: String) : AssignmentResult // Capacity exceeded
    data class Blocked(val reason: String) : AssignmentResult // Multicolor or security block
}

data class ProcessedEvent(
    val id: String, // UUID
    val originalEvent: EntryEvent,
    val result: AssignmentResult,
    val timestamp: Long = System.currentTimeMillis()
)
