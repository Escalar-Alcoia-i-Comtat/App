package org.escalaralcoiaicomtat.app.data.editable

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.generic.Ending
import org.escalaralcoiaicomtat.app.data.generic.EndingInclination
import org.escalaralcoiaicomtat.app.data.generic.EndingInfo
import org.escalaralcoiaicomtat.app.data.generic.GradeValue
import org.escalaralcoiaicomtat.app.data.generic.PitchInfo

@Serializable
data class EditablePitchInfo(
    val pitch: String = "",
    val grade: GradeValue? = null,
    val aidGrade: GradeValue? = null,
    val height: String = "",
    val ending: Ending? = null,
    val info: EndingInfo? = null,
    val inclination: EndingInclination? = null
) : Editable<PitchInfo> {
    constructor(
        pitch: UInt,
        grade: GradeValue?,
        aidGrade: GradeValue?,
        height: UInt?,
        ending: Ending?,
        info: EndingInfo?,
        inclination: EndingInclination?
    ) : this(pitch.toString(), grade, aidGrade, height?.toString() ?: "", ending, info, inclination)

    override fun validate(): Boolean {
        val pitch = pitch.toIntOrNull() ?: return false
        return pitch >= 0
    }

    override fun build(): PitchInfo {
        return PitchInfo(
            pitch = pitch.toUInt(),
            grade = grade,
            aidGrade = aidGrade,
            height = height.toUIntOrNull(),
            ending = ending,
            info = info,
            inclination = inclination
        )
    }
}
