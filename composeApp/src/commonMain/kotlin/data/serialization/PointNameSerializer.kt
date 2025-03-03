package data.serialization

import data.generic.Point
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PointNameSerializer : KSerializer<Point.Name> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "data.serialization.Point.Name",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Point.Name) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): Point.Name {
        val value = decoder.decodeString().uppercase()
        return Point.Name.valueOf(value)
    }
}
