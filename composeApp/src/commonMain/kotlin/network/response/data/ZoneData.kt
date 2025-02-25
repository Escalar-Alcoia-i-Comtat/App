@file:UseSerializers(UuidSerializer::class)

package network.response.data

import data.Zone
import data.generic.LatLng
import data.generic.Point
import data.serialization.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.uuid.Uuid

@Serializable
data class ZoneData(
    val id: Long,
    val timestamp: Long,
    @SerialName("display_name") val displayName: String,
    val image: Uuid,
    @SerialName("web_url") val webUrl: String,
    val kmz: Uuid,
    val point: LatLng? = null,
    val points: List<Point>,
    @SerialName("area_id") val parentAreaId: Long,
) : DataResponseType {
    /**
     * Converts the response into a [Zone].
     *
     * **[Zone.sectors] will be empty.**
     */
    fun asZone(): Zone = Zone(
        id,
        timestamp,
        displayName,
        image,
        webUrl,
        kmz,
        point,
        points,
        parentAreaId,
        emptyList()
    )
}
