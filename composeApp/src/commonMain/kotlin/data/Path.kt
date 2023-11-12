package data

import data.generic.Builder
import data.generic.Ending
import data.generic.PitchInfo
import data.model.DataTypeWithDisplayName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Path(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,

    @SerialName("sketch_id") val sketchId: UInt,

    val height: UInt? = null,
    val grade: String? = null,
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
): DataTypeWithDisplayName()
