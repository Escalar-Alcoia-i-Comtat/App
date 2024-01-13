package data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestArea {
    @Test
    fun `test area comparing`() {
        val area1 = DataTypeGenerator.newArea(id = 0L, displayName = "Area 1")
        val area1b = DataTypeGenerator.newArea(id = 1L, displayName = "Area 1")
        val area2 = DataTypeGenerator.newArea(id = 2L, displayName = "Area 2")
        val area3 = DataTypeGenerator.newArea(id = 3L, displayName = "Other")
        assertTrue { area1 < area2 }
        assertEquals(0, area1.compareTo(area1b))
        assertTrue { area3 > area1 }

        // Convert all the ids to a string of the ids joined by ,
        val string = listOf(area1, area1b, area2, area3)
            .sorted()
            .map { it.id }
            .joinToString(",")
        assertEquals("0,1,2,3", string)
    }
}
