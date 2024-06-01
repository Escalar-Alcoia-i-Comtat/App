package utils

import kotlin.test.Test
import kotlin.test.assertEquals

class StringUtils {
    @Test
    fun `test format - String`() {
        // One single replacement
        assertEquals("Hello, World!", "Hello, %s!".format("World"))
        // Multiple replacements
        assertEquals("Hello, World!", "%s, %s!".format("Hello", "World"))
        // No markers
        assertEquals("Hello, World!", "Hello, World!".format("None"))
    }

    @Test
    fun `test format - Int`() {
        // One single replacement
        assertEquals("Value: 10", "Value: %d".format(10))
        // Multiple replacements
        assertEquals("Value: 10, 50", "Value: %d, %d".format(10, 50))
        // No markers
        assertEquals("Value: 80", "Value: 80".format(10))
    }

    @Test
    fun `test format - Long`() {
        // One single replacement
        assertEquals("Value: 10", "Value: %d".format(10L))
        // Multiple replacements
        assertEquals("Value: 10, 50", "Value: %d, %d".format(10L, 50L))
        // No markers
        assertEquals("Value: 80", "Value: 80".format(10L))
    }

    @Test
    fun `test format - Float`() {
        // One single replacement
        assertEquals("Value: 10.0", "Value: %f".format(10f))
        // Multiple replacements
        assertEquals("Value: 10.0, 50.0", "Value: %f, %f".format(10f, 50f))
        // No markers
        assertEquals("Value: 80.0", "Value: 80.0".format(10f))

        // Decimal positions
        assertEquals("Value: 10.12", "Value: %.2f".format(10.123f))
        assertEquals("Value: 10.1", "Value: %.1f".format(10.123f))
        assertEquals("Value: 10", "Value: %.0f".format(10.123f))
    }

    @Test
    fun `test format - Double`() {
        // One single replacement
        assertEquals("Value: 10.0", "Value: %f".format(10.0))
        // Multiple replacements
        assertEquals("Value: 10.0, 50.0", "Value: %f, %f".format(10.0, 50.0))
        // No markers
        assertEquals("Value: 80.0", "Value: 80.0".format(10.0))

        // Decimal positions
        assertEquals("Value: 10.12", "Value: %.2f".format(10.123))
        assertEquals("Value: 10.1", "Value: %.1f".format(10.123))
        assertEquals("Value: 10", "Value: %.0f".format(10.123))
    }
}
