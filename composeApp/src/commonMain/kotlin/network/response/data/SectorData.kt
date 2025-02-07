package network.response.data

import data.Sector
import data.generic.ExternalTrack
import data.generic.LatLng
import data.generic.SunTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SectorData(
    val id: Long,
    val timestamp: Long,
    @SerialName("display_name") val displayName: String,
    val image: String,
    val gpx: String? = null,
    val tracks: List<ExternalTrack>? = null,
    @SerialName("kids_apt") val kidsApt: Boolean,
    val weight: String = "",
    @SerialName("walking_time") val walkingTime: Long? = null,
    val point: LatLng? = null,
    @SerialName("sun_time") val sunTime: SunTime,
    @SerialName("zone_id") val parentZoneId: Long,
) : DataResponseType {
    /**
     * Converts the response into a [Sector].
     *
     * **[Sector.paths] will be empty.**
     */
    fun asSector(): Sector = Sector(
        id,
        timestamp,
        displayName,
        image,
        gpx,
        tracks,
        kidsApt,
        weight,
        walkingTime,
        point,
        sunTime,
        parentZoneId,
        emptyList()
    )
}
