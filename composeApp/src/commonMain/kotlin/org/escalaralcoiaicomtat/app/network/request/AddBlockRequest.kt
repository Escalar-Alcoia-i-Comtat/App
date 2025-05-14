package org.escalaralcoiaicomtat.app.network.request

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.generic.BlockingRecurrenceYearly
import org.escalaralcoiaicomtat.app.data.generic.BlockingTypes

@Serializable
data class AddBlockRequest(
    val type: BlockingTypes? = null,
    val recurrence: BlockingRecurrenceYearly? = null,
    val endDate: LocalDateTime? = null
)
