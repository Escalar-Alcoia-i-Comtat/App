package data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestZone {
    @Test
    fun `test zone comparing`() {
        val zone1 = DataTypeGenerator.newZone(id = 0L, displayName = "Zone 1")
        val zone1b = DataTypeGenerator.newZone(id = 1L, displayName = "Zone 1")
        val zone2 = DataTypeGenerator.newZone(id = 2L, displayName = "Zone 2")
        val zone3 = DataTypeGenerator.newZone(id = 3L, displayName = "Other")
        assertTrue { zone1 < zone2 }
        assertEquals(0, zone1.compareTo(zone1b))
        assertTrue { zone3 < zone1 }

        // Convert all the ids to a string of the ids joined by ,
        val string = listOf(zone1, zone1b, zone2, zone3)
            .sorted()
            .map { it.id }
            .joinToString(",")
        assertEquals("3,0,1,2", string)
    }
}
