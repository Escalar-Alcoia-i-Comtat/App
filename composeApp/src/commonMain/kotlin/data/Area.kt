@file:UseSerializers(UuidSerializer::class)

package data

import data.serialization.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.uuid.Uuid

@Serializable
data class Area(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,
    // Nullable to allow editing without uploading, must never be null
    override val image: Uuid?,

    @Deprecated(
        "Should not be accessed, may be empty at any moment. Used just for fetching from server.",
        replaceWith = ReplaceWith(
            "DatabaseInterface.zones().all().filter { it.parentAreaId == this.id }",
            "database.DatabaseInterface"
        )
    )
    val zones: List<Zone>
) : DataTypeWithImage {
    override fun compareTo(other: DataType): Int {
        // Sort by displayName
        return displayName.compareTo(other.displayName)
    }

    override fun copy(id: Long, timestamp: Long, displayName: String): Area {
        return copy(id = id, timestamp = timestamp, displayName = displayName, image = image)
    }

    override fun copy(image: Uuid): Area {
        return copy(id = id, image = image)
    }
}
