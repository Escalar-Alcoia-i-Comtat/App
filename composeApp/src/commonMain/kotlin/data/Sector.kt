package data

import data.generic.ExternalTrack
import data.generic.LatLng
import data.generic.SunTime
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import network.Backend

@Serializable
data class Sector(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,
    override val image: String,
    val gpx: String? = null,
    val tracks: List<ExternalTrack>? = null,
    @SerialName("kids_apt") val kidsApt: Boolean,
    val weight: String = "",
    @SerialName("walking_time") val walkingTime: Long? = null,
    override val point: LatLng? = null,
    @SerialName("sun_time") val sunTime: SunTime,
    @SerialName("zone_id") val parentZoneId: Long,

    @Deprecated(
        "Should not be accessed, may be empty at any moment. Used just for fetching from server.",
        replaceWith = ReplaceWith(
            "DatabaseInterface.paths().all().filter { it.parentSectorId == this.id }",
            "database.DatabaseInterface"
        )
    )
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

    fun getGPXDownloadUrl(): Url? = gpx?.let(Backend::downloadFileUrl)

    /**
     * Checks whether the zone has any metadata to display.
     * @return `true` if either [point] is not null, or [tracks] is not null or empty.
     */
    override fun hasAnyMetadata(): Boolean {
        return super.hasAnyMetadata() || !tracks.isNullOrEmpty()
    }
}
