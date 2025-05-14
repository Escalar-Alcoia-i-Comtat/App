package data.generic

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.escalaralcoiaicomtat.app.data.generic.BlockingRecurrenceYearly
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestBlockingRecurrenceYearly {
    @Test
    fun testContains() {
        BlockingRecurrenceYearly(
            fromDay = 1U,
            fromMonth = Month.JANUARY,
            toDay = 15U,
            toMonth = Month.JANUARY,
        ).let { recurrence ->
            assertTrue { recurrence.contains(LocalDate.parse("2025-01-01")) }
            assertTrue { recurrence.contains(LocalDate.parse("2025-01-10")) }
            assertTrue { recurrence.contains(LocalDate.parse("2025-01-15")) }

            assertFalse { recurrence.contains(LocalDate.parse("2025-01-16")) }
            assertFalse { recurrence.contains(LocalDate.parse("2024-12-31")) }
            assertFalse { recurrence.contains(LocalDate.parse("2024-06-12")) }
            assertFalse { recurrence.contains(LocalDate.parse("2025-06-12")) }
        }

        BlockingRecurrenceYearly(
            fromDay = 1U,
            fromMonth = Month.FEBRUARY,
            toDay = 31U,
            toMonth = Month.MAY,
        ).let { recurrence ->
            assertTrue { recurrence.contains(LocalDate.parse("2025-02-01")) }
            assertTrue { recurrence.contains(LocalDate.parse("2025-04-10")) }
            assertTrue { recurrence.contains(LocalDate.parse("2025-05-31")) }

            assertFalse { recurrence.contains(LocalDate.parse("2025-01-31")) }
            assertFalse { recurrence.contains(LocalDate.parse("2025-06-01")) }
        }
    }
}
