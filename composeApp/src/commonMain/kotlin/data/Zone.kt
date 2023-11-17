package data

import data.generic.LatLng
import data.generic.Point
import data.model.DataTypeWithImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Zone(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,
    override val image: String,
    @SerialName("web_url") val webUrl: String,
    @SerialName("kmz") val kmzUUID: String,
    val point: LatLng? = null,
    val points: List<Point>,
    @SerialName("area_id") val parentAreaId: Long,
    val sectors: List<Sector>
): DataTypeWithImage()