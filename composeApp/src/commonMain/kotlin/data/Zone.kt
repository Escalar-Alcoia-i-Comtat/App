package data

import data.generic.LatLng
import data.generic.Point
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
    override val point: LatLng? = null,
    override val points: List<Point>,
    @SerialName("area_id") val parentAreaId: Long,

    @Deprecated(
        "Should not be accessed, may be empty at any moment. Used just for fetching from server.",
        replaceWith = ReplaceWith(
            "DatabaseInterface.sectors().all().filter { it.parentZoneId == this.id }",
            "database.DatabaseInterface"
        )
    )
    val sectors: List<Sector>
) : DataTypeWithImage, DataTypeWithPoint, DataTypeWithPoints, DataTypeWithParent {
    override fun compareTo(other: DataType): Int {
        return displayName.compareTo(other.displayName)
    }

    override fun getParentId(): Long = parentAreaId

    /**
     * Checks whether the zone has any metadata to display.
     * @return `true` if either [point] is not null, or [points] is not empty.
     */
    override fun hasAnyMetadata(): Boolean {
        return super.hasAnyMetadata() || points.isNotEmpty()
    }
}
