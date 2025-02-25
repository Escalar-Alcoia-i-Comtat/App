@file:UseSerializers(UuidSerializer::class)

package network.response.data

import data.Area
import data.serialization.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.uuid.Uuid

@Serializable
data class AreaData(
    val id: Long,
    val timestamp: Long,
    @SerialName("display_name") val displayName: String,
    val image: Uuid,
): DataResponseType {
    /**
     * Converts the response into an [Area].
     *
     * **[Area.zones] will be empty.**
     */
    fun asArea(): Area = Area(id, timestamp, displayName, image, emptyList())
}
