package org.escalaralcoiaicomtat.app.data.serialization

import kotlinx.datetime.format.DateTimeComponents
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object DateTimeComponentsSerializer : KSerializer<DateTimeComponents> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "org.escalaralcoiaicomtat.app.data.serialization.DateTimeComponents",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): DateTimeComponents {
        val raw = decoder.decodeString()
        return DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.parse(raw)
    }

    override fun serialize(encoder: Encoder, value: DateTimeComponents) {
        val raw = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.format(value)
        encoder.encodeString(raw)
    }
}
