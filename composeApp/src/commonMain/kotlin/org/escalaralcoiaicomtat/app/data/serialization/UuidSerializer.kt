package org.escalaralcoiaicomtat.app.data.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.uuid.Uuid

object UuidSerializer : KSerializer<Uuid> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "data.serialization.Uuid",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uuid {
        val value = decoder.decodeString().substringBefore('.')
        return try {
            Uuid.parse(value)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Could not decode UUID: $value", e)
        }
    }
}
