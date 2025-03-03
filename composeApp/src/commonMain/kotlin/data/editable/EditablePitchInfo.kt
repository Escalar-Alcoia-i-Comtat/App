package data.editable

import data.generic.Ending
import data.generic.EndingInclination
import data.generic.EndingInfo
import data.generic.GradeValue
import data.generic.PitchInfo
import kotlinx.serialization.Serializable

@Serializable
data class EditablePitchInfo(
    val pitch: String = "",
    val grade: GradeValue? = null,
    val height: String = "",
    val ending: Ending? = null,
    val info: EndingInfo? = null,
    val inclination: EndingInclination? = null
) : Editable<PitchInfo> {
    constructor(
        pitch: UInt,
        grade: GradeValue?,
        height: UInt?,
        ending: Ending?,
        info: EndingInfo?,
        inclination: EndingInclination?
    ) : this(pitch.toString(), grade, height?.toString() ?: "", ending, info, inclination)

    override fun validate(): Boolean {
        val pitch = pitch.toIntOrNull() ?: return false
        return pitch >= 0
    }

    override fun build(): PitchInfo {
        return PitchInfo(
            pitch = pitch.toUInt(),
            grade = grade.toString(),
            height = height.toUIntOrNull(),
            ending = ending,
            info = info,
            inclination = inclination
        )
    }
}
