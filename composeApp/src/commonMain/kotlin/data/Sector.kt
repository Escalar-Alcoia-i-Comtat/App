package data

import data.generic.LatLng
import data.model.DataTypeWithDisplayName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sector(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,
    val image: String,
    @SerialName("kids_apt") val kidsApt: Boolean,
    val weight: String,
    @SerialName("walking_time") val walkingTime: Long? = null,
    val point: LatLng? = null,
    @SerialName("sun_time") val sunTime: String,
    @SerialName("zone_id") val parentZoneId: Long,

    val paths: List<Path>
): DataTypeWithDisplayName()
