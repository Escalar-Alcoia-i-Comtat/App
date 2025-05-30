@file:UseSerializers(UuidSerializer::class)

package org.escalaralcoiaicomtat.app.network.response.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.generic.ExternalTrack
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.data.generic.SunTime
import org.escalaralcoiaicomtat.app.data.serialization.UuidSerializer
import kotlin.uuid.Uuid

@Serializable
data class SectorData(
    val id: Long,
    val timestamp: Long,
    @SerialName("display_name") val displayName: String,
    val image: Uuid,
    val gpx: Uuid? = null,
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
