package data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestSector {
    @Test
    fun `test sector comparing`() {
        val sector1 = DataTypeGenerator.newSector(id = 0L, displayName = "Sector 1", weight = "aaa")
        val sector1b = DataTypeGenerator.newSector(id = 1L, displayName = "Sector 1", weight = "aaa")
        val sector2 = DataTypeGenerator.newSector(id = 2L, displayName = "Sector 2", weight = "aaa")
        val sector3 = DataTypeGenerator.newSector(id = 3L, displayName = "Sector 2", weight = "aac")
        val sector4 = DataTypeGenerator.newSector(id = 4L, displayName = "Other", weight = "aab")
        val sector5 = DataTypeGenerator.newSector(id = 5L, displayName = "Other")
        val sector6 = DataTypeGenerator.newSector(id = 6L, displayName = "Other", weight = "aac")
        assertTrue { sector1 < sector2 }
        assertEquals(0, sector1.compareTo(sector1b))
        assertTrue { sector3 > sector2 }
        assertTrue { sector4 < sector3 }
        assertTrue { sector6 > sector4 }

        // Convert all the ids to a string of the ids joined by ,
        val string = listOf(sector1, sector2, sector3, sector4, sector5, sector6)
            .sorted()
            .map { it.id }
            .joinToString(",")
        assertEquals("0,2,4,6,3,5", string)
    }
}
