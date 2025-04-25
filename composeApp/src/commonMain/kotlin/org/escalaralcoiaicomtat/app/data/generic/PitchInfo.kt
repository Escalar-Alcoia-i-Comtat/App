package org.escalaralcoiaicomtat.app.data.generic

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.editable.EditablePitchInfo

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
