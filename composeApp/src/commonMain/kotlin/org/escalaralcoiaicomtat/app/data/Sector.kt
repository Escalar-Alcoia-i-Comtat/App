@file:UseSerializers(UuidSerializer::class)

package org.escalaralcoiaicomtat.app.data

import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.escalaralcoiaicomtat.app.data.generic.ExternalTrack
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.data.generic.PhoneSignalAvailability
import org.escalaralcoiaicomtat.app.data.generic.SunTime
import org.escalaralcoiaicomtat.app.data.serialization.UuidSerializer
import org.escalaralcoiaicomtat.app.network.BasicBackend
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@ExperimentalUuidApi
data class Sector(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,
    // Nullable to allow editing without uploading, must never be null
    override val image: Uuid?,
    override val gpx: Uuid? = null,
    val tracks: List<ExternalTrack>? = null,
    @SerialName("kids_apt") val kidsApt: Boolean,
    val weight: String = "",
    @SerialName("walking_time") val walkingTime: Long? = null,
    @SerialName("phone_signal_availability") val phoneSignalAvailability: List<PhoneSignalAvailability>?,
    override val point: LatLng? = null,
    @SerialName("sun_time") val sunTime: SunTime,
    @SerialName("zone_id") val parentZoneId: Long,

    @Deprecated(
        "Should not be accessed, may be empty at any moment. Used just for fetching from server.",
        replaceWith = ReplaceWith(
            "DatabaseInterface.paths().all().filter { it.parentSectorId == this.id }",
            "org.escalaralcoiaicomtat.app.DatabaseInterface"
        )
    )
    val paths: List<Path>? = null
) : DataTypeWithImage, DataTypeWithGPX, DataTypeWithPoint, DataTypeWithParent {
    override fun compareTo(other: DataType): Int {
        return (other as? Sector)
            // If other is a Sector, try to compare by weight, but don't consider empty weights
            ?.takeUnless { weight.isBlank() || other.weight.isBlank() }
            ?.let { weight.compareTo(other.weight) }
            // If they are equal, don't take, and fallback to displayName
            ?.takeIf { it != 0 }
            ?: displayName.compareTo(other.displayName)
    }

    override val parentId: Long get() = parentZoneId

    fun getGPXDownloadUrl(): Url? = gpx?.let(BasicBackend::downloadFileUrl)

    /**
     * Checks whether the zone has any metadata to display.
     * @return `true` if either [point] is not null, or [tracks] is not null or empty.
     */
    override fun hasAnyMetadata(): Boolean {
        return super.hasAnyMetadata() || !tracks.isNullOrEmpty()
    }

    override fun copy(id: Long, timestamp: Long): Sector {
        return copy(id = id, timestamp = timestamp, image = image)
    }

    override fun copy(displayName: String): Sector {
        return copy(id = id, displayName = displayName)
    }

    override fun copy(image: Uuid): Sector {
        return copy(id = id, image = image)
    }

    override fun copyGpx(gpx: Uuid): DataTypeWithGPX {
        return copy(id = id, gpx = gpx)
    }

    override fun copy(parentId: Long): Sector {
        return copy(id = id, parentZoneId = parentId)
    }

    override fun copy(point: LatLng?): Sector {
        return copy(id = id, point = point)
    }
}
