@file:UseSerializers(UuidSerializer::class)

package org.escalaralcoiaicomtat.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.escalaralcoiaicomtat.app.data.serialization.UuidSerializer
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
            "org.escalaralcoiaicomtat.app.DatabaseInterface"
        )
    )
    val zones: List<Zone>? = null
) : DataTypeWithImage {
    override fun compareTo(other: DataType): Int {
        // Sort by displayName
        return displayName.compareTo(other.displayName)
    }

    override fun copy(id: Long, timestamp: Long): Area {
        return copy(id = id, timestamp = timestamp, displayName = displayName, image = image)
    }

    override fun copy(displayName: String): Area {
        return copy(id = id, displayName = displayName)
    }

    override fun copy(image: Uuid): Area {
        return copy(id = id, image = image)
    }
}
