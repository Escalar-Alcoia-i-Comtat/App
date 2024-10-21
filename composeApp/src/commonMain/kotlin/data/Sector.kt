package data

import data.generic.LatLng
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sector(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,
    override val image: String,
    val gpx: String? = null,
    @SerialName("kids_apt") val kidsApt: Boolean,
    val weight: String = "",
    @SerialName("walking_time") val walkingTime: Long? = null,
    override val point: LatLng? = null,
    @SerialName("sun_time") val sunTime: String?,
    @SerialName("zone_id") val parentZoneId: Long,
    val paths: List<Path>
): DataTypeWithImage, DataTypeWithPoint, DataTypeWithParent {
    override fun compareTo(other: DataType): Int {
        return (other as? Sector)
            // If other is a Sector, try to compare by weight, but don't consider empty weights
            ?.takeUnless { weight.isBlank() || other.weight.isBlank() }
            ?.let { weight.compareTo(other.weight) }
            // If they are equal, don't take, and fallback to displayName
            ?.takeIf { it != 0 }
            ?: displayName.compareTo(other.displayName)
    }

    override fun getParentId(): Long = parentZoneId
}
