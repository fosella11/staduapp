package com.domingame.staduapp.feature.stadium.domain.model

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
