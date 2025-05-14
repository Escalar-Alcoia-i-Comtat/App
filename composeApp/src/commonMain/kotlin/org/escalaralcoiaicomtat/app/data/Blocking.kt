package org.escalaralcoiaicomtat.app.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Clock.System
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.generic.BlockingRecurrenceYearly
import org.escalaralcoiaicomtat.app.data.generic.BlockingTypes

@Serializable
data class Blocking(
    override val id: Long,
    override val timestamp: Long,
    val type: BlockingTypes,
    val recurrence: BlockingRecurrenceYearly? = null,
    @SerialName("end_date") val endDate: LocalDateTime? = null,
    @SerialName("path_id") val pathId: Long,
) : Entity {
    override fun copy(
        id: Long,
        timestamp: Long,
    ): Blocking = copy(id = id, timestamp = timestamp, type = type)

    fun isActive(
        clock: Clock = System,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): Boolean {
        val now = clock.now()
        return when {
            endDate != null -> {
                endDate.toInstant(timeZone) > now
            }
            recurrence != null -> {
                recurrence.contains(now.toLocalDateTime(timeZone).date)
            }
            else -> true
        }
    }
}
