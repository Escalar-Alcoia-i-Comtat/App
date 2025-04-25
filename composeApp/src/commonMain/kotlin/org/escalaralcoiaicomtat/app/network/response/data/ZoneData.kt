@file:UseSerializers(UuidSerializer::class)

package org.escalaralcoiaicomtat.app.network.response.data

import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.data.generic.Point
import org.escalaralcoiaicomtat.app.data.serialization.UuidSerializer
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
        kmz,
        point,
        points,
        parentAreaId,
        emptyList()
    )
}
