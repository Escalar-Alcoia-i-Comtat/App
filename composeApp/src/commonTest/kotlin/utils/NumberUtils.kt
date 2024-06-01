package utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumberUtils {
    @Test
    fun `round - Double`() {
        assertEquals(1.23, 1.23456.round(2))
        assertEquals(1.23456, 1.23456.round(-1))
    }

    @Test
    fun `round - Float`() {
        assertEquals(1.23f, 1.23456f.round(2))
        assertEquals(1.23456f, 1.23456f.round(-1))
    }

    @Test
    fun `isNullOrZero - Float`() {
        var variable: Float? = null
        assertTrue { variable.isNullOrZero() }

        variable = 0.0f
        assertTrue { variable.isNullOrZero() }

        variable = 10.0f
        assertFalse { variable.isNullOrZero() }
    }
}
