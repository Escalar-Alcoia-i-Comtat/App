@file:UseSerializers(UuidSerializer::class)

package org.escalaralcoiaicomtat.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.data.generic.Point
import org.escalaralcoiaicomtat.app.data.serialization.UuidSerializer
import kotlin.uuid.Uuid

@Serializable
data class Zone(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,
    // Nullable to allow editing without uploading, must never be null
    override val image: Uuid?,
    // Nullable to allow editing without uploading, must never be null
    override val kmz: Uuid?,
    override val point: LatLng? = null,
    override val points: List<Point>,
    @SerialName("area_id") val parentAreaId: Long,

    @Deprecated(
        "Should not be accessed, may be empty at any moment. Used just for fetching from server.",
        replaceWith = ReplaceWith(
            "DatabaseInterface.sectors().all().filter { it.parentZoneId == this.id }",
            "org.escalaralcoiaicomtat.app.DatabaseInterface"
        )
    )
    val sectors: List<Sector>? = null
) : DataTypeWithImage, DataTypeWithKMZ, DataTypeWithPoint, DataTypeWithPoints, DataTypeWithParent {
    override fun compareTo(other: DataType): Int {
        return displayName.compareTo(other.displayName)
    }

    override val parentId: Long get() = parentAreaId

    /**
     * Checks whether the zone has any metadata to display.
     * @return `true` if either [point] is not null, or [points] is not empty.
     */
    override fun hasAnyMetadata(): Boolean {
        return super.hasAnyMetadata() || points.isNotEmpty()
    }

    override fun copy(id: Long, timestamp: Long, displayName: String): Zone {
        return copy(id = id, timestamp = timestamp, displayName = displayName, image = image)
    }

    override fun copy(image: Uuid): Zone {
        return copy(id = id, image = image)
    }

    override fun copyKmz(kmz: Uuid): DataTypeWithKMZ {
        return copy(id = id, kmz = kmz)
    }

    override fun copy(parentId: Long): Zone {
        return copy(id = id, parentAreaId = parentId)
    }

    override fun copy(point: LatLng?): Zone {
        return copy(id = id, point = point)
    }

    override fun copy(points: List<Point>): Zone {
        return copy(id = id, points = points)
    }
}
