package data

import data.generic.Builder
import data.generic.Ending
import data.generic.GradeValue
import data.generic.PitchInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.isNotNullOrZero

@Serializable
data class Path(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,

    @SerialName("sketch_id") val sketchId: UInt,

    val height: UInt? = null,
    @SerialName("grade") val gradeValue: String? = null,
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

    val images: List<String>? = null,

    @SerialName("sector_id") val parentSectorId: Long
) : DataType, DataTypeWithParent {
    val grade: GradeValue? get() = gradeValue?.let(GradeValue::fromString)

    val hasAnyTypeCount: Boolean = paraboltCount.isNotNullOrZero() ||
        burilCount.isNotNullOrZero() ||
        pitonCount.isNotNullOrZero() ||
        spitCount.isNotNullOrZero() ||
        tensorCount.isNotNullOrZero()

    val hasAnyCount: Boolean = stringCount.isNotNullOrZero() || hasAnyTypeCount

    override fun compareTo(other: DataType): Int {
        return (other as? data.Path)
            // If other is a Path, try to compare by sketchId
            ?.let { sketchId.compareTo(other.sketchId) }
            // If they are equal, don't take, and fallback to displayName
            ?.takeIf { it != 0 }
            ?: displayName.compareTo(other.displayName)
    }

    override fun getParentId(): Long = parentSectorId

    override fun copy(id: Long, timestamp: Long, displayName: String): Path {
        return copy(id = id, timestamp = timestamp, displayName = displayName, sketchId = sketchId)
    }

    override fun copy(parentId: Long): Path {
        return copy(id = id, parentSectorId = parentId)
    }
}
