package org.escalaralcoiaicomtat.app.data.generic

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.editable.EditablePitchInfo
import org.escalaralcoiaicomtat.app.data.serialization.GradeSerializer

@Serializable
data class PitchInfo(
    val pitch: UInt,
    @Serializable(GradeSerializer::class) val grade: GradeValue? = null,
    val height: UInt? = null,
    val ending: Ending? = null,
    val info: EndingInfo? = null,
    val inclination: EndingInclination? = null
) {
    fun editable() = EditablePitchInfo(
        pitch,
        grade,
        height,
        ending,
        info,
        inclination,
    )
}
