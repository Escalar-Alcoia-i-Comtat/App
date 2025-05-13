package org.escalaralcoiaicomtat.app.data

import kotlinx.datetime.LocalDateTime
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
    @SerialName("path_id") val pathId: Int,
) : Entity {
    override fun copy(
        id: Long,
        timestamp: Long,
    ): Blocking = copy(id = id, timestamp = timestamp, type = type)
}
