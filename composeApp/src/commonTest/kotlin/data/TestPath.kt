package data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestPath {
    @Test
    fun `test path comparing`() {
        val path1 = DataTypeGenerator.newPath(id = 0L, displayName = "Path 1", sketchId = 0U)
        val path1b = DataTypeGenerator.newPath(id = 1L, displayName = "Path 1", sketchId = 0U)
        val path2 = DataTypeGenerator.newPath(id = 2L, displayName = "Path 2", sketchId = 1U)
        val path3 = DataTypeGenerator.newPath(id = 3L, displayName = "Other", sketchId = 2U)
        val path4 = DataTypeGenerator.newPath(id = 4L, displayName = "Other 2", sketchId = 2U)
        assertTrue { path1 < path2 }
        assertEquals(0, path1.compareTo(path1b))
        assertTrue { path2 < path3 }
        assertTrue { path3 < path4 }

        // Convert all the ids to a string of the ids joined by ,
        val string = listOf(path1b, path2, path3, path4)
            .sorted()
            .map { it.id }
            .joinToString(",")
        assertEquals("1,2,3,4", string)
    }
}
