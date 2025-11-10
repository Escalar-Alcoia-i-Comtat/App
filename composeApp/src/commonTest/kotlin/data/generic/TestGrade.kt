package data.generic

import org.escalaralcoiaicomtat.app.data.generic.AidGrade
import org.escalaralcoiaicomtat.app.data.generic.GradeValue
import org.escalaralcoiaicomtat.app.data.generic.SportsGrade
import kotlin.test.Test
import kotlin.test.assertEquals

class TestGrade {
    @Test
    fun `test converting sports grade to string`() {
        assertEquals("5º", SportsGrade.G5.toString())
        assertEquals("6b+", SportsGrade.G6B_PLUS.toString())
        assertEquals("1º", SportsGrade.G1.toString())
        assertEquals("4º", SportsGrade.G4.toString())
        assertEquals("7c", SportsGrade.G7C.toString())
        assertEquals("¿?", SportsGrade.UNKNOWN.toString())
    }

    @Test
    fun `test converting aid grade to string`() {
        assertEquals("A0", AidGrade.A0.toString())
        assertEquals("A3+", AidGrade.A3_PLUS.toString())
        assertEquals("Ae", AidGrade.A_EQUIPPED.toString())
    }

    @Test
    fun `test GradeValue_fromString string`() {
        assertEquals(SportsGrade.G5A, GradeValue.fromString("5º"))
        assertEquals(SportsGrade.G4_PLUS, GradeValue.fromString("4+"))
        assertEquals(SportsGrade.G6B, GradeValue.fromString("6b"))
        assertEquals(SportsGrade.G8C_PLUS, GradeValue.fromString("8c+"))
        assertEquals(AidGrade.A3, GradeValue.fromString("A3"))
        assertEquals(AidGrade.A5_PLUS, GradeValue.fromString("A5+"))
        assertEquals(AidGrade.A_EQUIPPED, GradeValue.fromString("Ae"))
    }

    @Test
    fun `test GradeValue_fromString name`() {
        assertEquals(SportsGrade.G5, GradeValue.fromString(SportsGrade.G5.name))
        assertEquals(SportsGrade.G4_PLUS, GradeValue.fromString(SportsGrade.G4_PLUS.name))
        assertEquals(SportsGrade.G6B, GradeValue.fromString(SportsGrade.G6B.name))
        assertEquals(SportsGrade.G8C_PLUS, GradeValue.fromString(SportsGrade.G8C_PLUS.name))
        assertEquals(AidGrade.A3, GradeValue.fromString(AidGrade.A3.name))
        assertEquals(AidGrade.A5_PLUS, GradeValue.fromString(AidGrade.A5_PLUS.name))
        assertEquals(AidGrade.A_EQUIPPED, GradeValue.fromString(AidGrade.A_EQUIPPED.name))
    }
}
