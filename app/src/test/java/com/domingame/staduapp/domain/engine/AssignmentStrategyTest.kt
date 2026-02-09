package com.domingame.staduapp.domain.engine

import com.domingame.staduapp.domain.model.*
import com.domingame.staduapp.domain.StaduConfig
import org.junit.Assert.*
import org.junit.Test 

class AssignmentStrategyTest {

    @Test
    fun testMulticolorBlocked() {
        // Historia MULTICOLOR: bloquear acceso
        val event = EntryEvent("ENTRY", "Gate A", "MULTICOLOR")
        val state = createEmptyState()
        val result = AssignmentStrategy.determineAssignment(state, event)
        
        assertTrue("Multicolor should be Blocked", result is AssignmentResult.Blocked)
    }

    @Test
    fun testBlueShirtForcesNorth() {
        // Regla BLUE: forzar asignación obligatoria a Norte
        val event = EntryEvent("ENTRY", "Gate C", "BLUE") // Gate C would normally be East
        val state = createEmptyState()
        val result = AssignmentStrategy.determineAssignment(state, event)
        
        assertTrue(result is AssignmentResult.Success)
        assertEquals("Should assign to North sector", SectorName.NORTH, (result as AssignmentResult.Success).sector)
    }

    @Test
    fun testStandardAssignment() {
        // Asignación estándar: sector de la puerta
        val event = EntryEvent("ENTRY", "Gate A", "RED") // Gate A is North
        val state = createEmptyState()
        
        val result = AssignmentStrategy.determineAssignment(state, event)
        
        assertTrue(result is AssignmentResult.Success)
        assertEquals(SectorName.NORTH, (result as AssignmentResult.Success).sector)
    }

    private fun createEmptyState(): StadiumState {
        val sectors = SectorName.values().associateWith { sectorName ->
            val blocks = BlockName.values().associateWith { blockName ->
                BlockState(name = blockName, capacity = StaduConfig.BLOCK_CAPACITY)
            }
            SectorState(name = sectorName, blocks = blocks)
        }
        return StadiumState(sectors = sectors, metrics = GlobalMetrics())
    }
}
