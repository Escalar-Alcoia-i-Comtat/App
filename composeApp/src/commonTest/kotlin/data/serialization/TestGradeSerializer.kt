package data.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonPrimitive
import org.escalaralcoiaicomtat.app.data.generic.SportsGrade
import org.escalaralcoiaicomtat.app.data.serialization.GradeSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestGradeSerializer {
    @Test
    fun `test serialization`() {
        Json.encodeToJsonElement(GradeSerializer, SportsGrade.G6A_PLUS).jsonPrimitive.let { grade ->
            assertTrue { grade.isString }
            assertEquals("G6A_PLUS", grade.content)
        }
    }
}
