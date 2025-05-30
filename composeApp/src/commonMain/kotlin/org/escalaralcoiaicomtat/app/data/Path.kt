@file:UseSerializers(UuidSerializer::class)

package org.escalaralcoiaicomtat.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.escalaralcoiaicomtat.app.data.generic.Builder
import org.escalaralcoiaicomtat.app.data.generic.Ending
import org.escalaralcoiaicomtat.app.data.generic.GradeValue
import org.escalaralcoiaicomtat.app.data.generic.PitchInfo
import org.escalaralcoiaicomtat.app.data.generic.SafesCount
import org.escalaralcoiaicomtat.app.data.serialization.GradeSerializer
import org.escalaralcoiaicomtat.app.data.serialization.UuidSerializer
import kotlin.uuid.Uuid

@Serializable
data class Path(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,

    @SerialName("sketch_id") val sketchId: UInt,

    val height: UInt? = null,
    @Serializable(GradeSerializer::class) val grade: GradeValue? = null,
    @Serializable(GradeSerializer::class) @SerialName("aid_grade") val aidGrade: GradeValue? = null,
    val ending: Ending? = null,
    val pitches: List<PitchInfo>? = null,

    @SerialName("string_count") val stringCount: UInt? = null,

    @SerialName("parabolt_count") val paraboltCount: UInt? = null,
    @SerialName("buril_count") val burilCount: UInt? = null,
    @SerialName("piton_count") val pitonCount: UInt? = null,
    @SerialName("spit_count") val spitCount: UInt? = null,
    @SerialName("tensor_count") val tensorCount: UInt? = null,

    @SerialName("cracker_required") val nutRequired: Boolean,
    @SerialName("friend_required") val friendRequired: Boolean,
    @SerialName("lanyard_required") val lanyardRequired: Boolean,
    @SerialName("nail_required") val nailRequired: Boolean,
    @SerialName("piton_required") val pitonRequired: Boolean,
    @SerialName("stapes_required") val stapesRequired: Boolean,

    @SerialName("show_description") val showDescription: Boolean,
    val description: String? = null,

    val builder: Builder? = null,
    @SerialName("re_builder") val reBuilders: List<Builder>? = null,

    val images: List<Uuid>? = null,

    @SerialName("sector_id") val parentSectorId: Long
) : DataType, DataTypeWithParent {
    val safes: SafesCount get() = SafesCount(this)

    override fun compareTo(other: DataType): Int {
        return (other as? Path)
            // If other is a Path, try to compare by sketchId
            ?.let { sketchId.compareTo(other.sketchId) }
            // If they are equal, don't take, and fallback to displayName
            ?.takeIf { it != 0 }
            ?: displayName.compareTo(other.displayName)
    }

    override val parentId: Long get() = parentSectorId

    override fun copy(id: Long, timestamp: Long): Path {
        return copy(id = id, timestamp = timestamp, sketchId = sketchId)
    }

    override fun copy(displayName: String): Path {
        return copy(id = id, displayName = displayName)
    }

    override fun copy(parentId: Long): Path {
        return copy(id = id, parentSectorId = parentId)
    }
}
