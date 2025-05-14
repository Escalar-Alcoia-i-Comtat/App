package org.escalaralcoiaicomtat.app.data.generic

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlinx.serialization.Serializable

@Serializable
data class BlockingRecurrenceYearly(
    val fromDay: UShort,
    val fromMonth: Month,
    val toDay: UShort,
    val toMonth: Month,
) {
    /**
     * Checks whether the given date is inside the defined range.
     */
    fun contains(localDate: LocalDate): Boolean {
        val currentMonth = localDate.month
        val currentDay = localDate.dayOfMonth

        val startMonth = fromMonth
        val startDay = fromDay.toInt()
        val endMonth = toMonth
        val endDay = toDay.toInt()

        // Helper to compare Month-Day pairs lexicographically
        fun isAfterOrEqual(m1: Month, d1: Int, m2: Month, d2: Int): Boolean =
            m1.ordinal > m2.ordinal || (m1 == m2 && d1 >= d2)

        fun isBeforeOrEqual(m1: Month, d1: Int, m2: Month, d2: Int): Boolean =
            m1.ordinal < m2.ordinal || (m1 == m2 && d1 <= d2)

        return if (
            // Non-wrapping range: start <= end
            isAfterOrEqual(endMonth, endDay, startMonth, startDay)
        ) {
            // Date must be between start and end inclusive
            isAfterOrEqual(currentMonth, currentDay, startMonth, startDay) &&
                    isBeforeOrEqual(currentMonth, currentDay, endMonth, endDay)
        } else {
            // Wrapping range: e.g., Nov 1 to Feb 28
            isAfterOrEqual(currentMonth, currentDay, startMonth, startDay) ||
                    isBeforeOrEqual(currentMonth, currentDay, endMonth, endDay)
        }
    }

    fun from(): String = "$fromDay/${fromMonth.number.toString().padStart(2, '0')}"

    fun to(): String = "$toDay/${toMonth.number.toString().padStart(2, '0')}"
}
