package data.generic

import kotlin.test.Test
import kotlin.test.assertEquals

class TestGrade {
    @Test
    fun `test converting grade to string`() {
        assertEquals("6b+", SportsGrade.G6B_PLUS.toString())
        assertEquals("1º", SportsGrade.G1.toString())
        assertEquals("4º", SportsGrade.G4.toString())
        assertEquals("7c", SportsGrade.G7C.toString())
    }
}
