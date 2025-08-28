package org.escalaralcoiaicomtat.app.data.generic

import kotlinx.serialization.Serializable

@Serializable
data class PhoneSignalAvailability(
    val strength: PhoneSignalStrength,
    val carrier: PhoneCarrier,
)
