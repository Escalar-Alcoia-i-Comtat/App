package network.response.data

import data.Zone
import data.generic.LatLng
import data.generic.Point
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZoneData(
    val id: Long,
    val timestamp: Long,
    @SerialName("display_name") val displayName: String,
    val image: String,
    @SerialName("web_url") val webUrl: String,
    @SerialName("kmz") val kmzUUID: String,
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
        kmzUUID,
        point,
        points,
        parentAreaId,
        emptyList()
    )
}
