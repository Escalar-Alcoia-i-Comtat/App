package org.escalaralcoiaicomtat.app.data.generic

import kotlinx.datetime.Month
import kotlinx.serialization.Serializable

@Serializable
data class BlockingRecurrenceYearly(
    val fromDay: UShort,
    val fromMonth: Month,
    val toDay: UShort,
    val toMonth: Month,
)
