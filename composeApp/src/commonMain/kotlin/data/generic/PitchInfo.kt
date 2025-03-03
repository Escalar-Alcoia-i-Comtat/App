package data.generic

import data.editable.EditablePitchInfo
import kotlinx.serialization.Serializable

@Serializable
data class PitchInfo(
    val pitch: UInt,
    val grade: String? = null,
    val height: UInt? = null,
    val ending: Ending? = null,
    val info: EndingInfo? = null,
    val inclination: EndingInclination? = null
) {
    fun editable() = EditablePitchInfo(
        pitch,
        grade?.let(GradeValue::fromString),
        height,
        ending,
        info,
        inclination,
    )
}
