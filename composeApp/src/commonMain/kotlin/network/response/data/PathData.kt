@file:UseSerializers(UuidSerializer::class)

package network.response.data

import data.Path
import data.generic.Builder
import data.generic.Ending
import data.generic.PitchInfo
import data.serialization.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.uuid.Uuid

@Serializable
data class PathData(
    val id: Long,
    val timestamp: Long,
    @SerialName("display_name") val displayName: String,

    @SerialName("sketch_id") val sketchId: UInt,

    val height: UInt?,
    @SerialName("grade") val gradeValue: String?,
    val ending: Ending?,
    val pitches: List<PitchInfo>?,

    @SerialName("string_count") val stringCount: UInt?,

    @SerialName("parabolt_count") val paraboltCount: UInt?,
    @SerialName("buril_count") val burilCount: UInt?,
    @SerialName("piton_count") val pitonCount: UInt?,
    @SerialName("spit_count") val spitCount: UInt?,
    @SerialName("tensor_count") val tensorCount: UInt?,

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
) : DataResponseType {
    /**
     * Converts the response into a [Path].
     */
    fun asPath(): Path = Path(
        id,
        timestamp,
        displayName,
        sketchId,
        height,
        gradeValue,
        ending,
        pitches,
        stringCount,
        paraboltCount,
        burilCount,
        pitonCount,
        spitCount,
        tensorCount,
        nutRequired,
        friendRequired,
        lanyardRequired,
        nailRequired,
        pitonRequired,
        stapesRequired,
        showDescription,
        description,
        builder,
        reBuilders,
        images,
        parentSectorId
    )
}
