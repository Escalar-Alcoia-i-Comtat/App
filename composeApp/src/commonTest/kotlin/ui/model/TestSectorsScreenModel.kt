package ui.model

import TestSuite
import data.DataTypeGenerator
import org.escalaralcoiaicomtat.app.ui.model.SectorsScreenModel
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSectorsScreenModel: TestSuite() {
    @Test
    fun test_moveItem() = test {
        val model = SectorsScreenModel()
        model._children.value = listOf(
            DataTypeGenerator.newSector(id = 1L, displayName = "Sector 1", weight = "aaa"),
            DataTypeGenerator.newSector(id = 2L, displayName = "Sector 2", weight = "aab"),
            DataTypeGenerator.newSector(id = 3L, displayName = "Sector 3", weight = "aac"),
        )

        model.moveItem(0, 2)

        val children = model._children.value.orEmpty()
        children[0].let {
            assertEquals(2L, it.id)
            assertEquals("0", it.weight)
        }
        children[1].let {
            assertEquals(3L, it.id)
            assertEquals("1", it.weight)
        }
        children[2].let {
            assertEquals(1L, it.id)
            assertEquals("2", it.weight)
        }
    }
}
