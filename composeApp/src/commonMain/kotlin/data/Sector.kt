package data

import data.generic.LatLng
import database.Sector
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sector(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,
    override val image: String,
    @SerialName("kids_apt") val kidsApt: Boolean,
    val weight: String,
    @SerialName("walking_time") val walkingTime: Long? = null,
    override val point: LatLng? = null,
    @SerialName("sun_time") val sunTime: String?,
    @SerialName("zone_id") val parentZoneId: Long,
    val paths: List<Path>
): DataTypeWithImage, DataTypeWithPoint {
    constructor(sector: Sector): this(
        sector.id,
        sector.timestamp,
        sector.displayName,
        sector.image,
        sector.kidsApt,
        sector.weight,
        sector.walkingTime,
        sector.point,
        sector.sunTime,
        sector.parentZoneId,
        emptyList()
    )

    override fun compareTo(other: DataType): Int {
        return (other as? data.Sector)
            // If other is a Sector, try to compare by weight, but don't consider empty weights
            ?.takeUnless { weight.isBlank() || other.weight.isBlank() }
            ?.let { weight.compareTo(other.weight) }
            // If they are equal, don't take, and fallback to displayName
            ?.takeIf { it != 0 }
            ?: displayName.compareTo(other.displayName)
    }
}
