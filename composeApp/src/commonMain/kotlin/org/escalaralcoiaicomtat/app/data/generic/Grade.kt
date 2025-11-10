@file:Suppress("unused")

package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.escalaralcoiaicomtat.app.ui.color.ColorGroup
import org.escalaralcoiaicomtat.app.ui.theme.*

interface GradeValue {
    companion object {
        fun fromString(value: String): GradeValue {
            val name = value
                .uppercase()
                .replace("+", "_PLUS")
                .replace("E", "_EQUIPPED")
                .replace("º", "A")
            return SportsGrade.entries.find { it.name.endsWith(name) }
                ?: AidGrade.entries.find { it.name == name }
                ?: SportsGrade.UNKNOWN
        }
    }

    val name: String

    fun asString(): String

    @Composable
    fun toAnnotatedString(): AnnotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = color.current)) { append(asString()) }
    }
}

val GradeValue?.color: ColorGroup
    get() =
        if (this is SportsGrade) {
            val number = name[1].digitToIntOrNull()
            if (number == null)
                ColorGradeP
            else if (number <= 5)
                ColorGrade1
            else if (number <= 6)
                ColorGrade2
            else if (number <= 7)
                ColorGrade3
            else
                ColorGrade4
        } else if (this is AidGrade) {
            ColorGradeA
        } else {
            ColorGradeP
        }

enum class SportsGrade : GradeValue {
    G1,
    G2, G2_PLUS,
    G3A, G3B, G3C, G3_PLUS, G3,
    G4A, G4B, G4C, G4_PLUS, G4,
    G5A, G5B, G5C, G5_PLUS, G5,
    G6A, G6A_PLUS, G6B, G6B_PLUS, G6C, G6C_PLUS,
    G7A, G7A_PLUS, G7B, G7B_PLUS, G7C, G7C_PLUS,
    G8A, G8A_PLUS, G8B, G8B_PLUS, G8C, G8C_PLUS,
    G9A, G9A_PLUS, G9B, G9B_PLUS, G9C, G9C_PLUS,
    UNKNOWN;

    override fun asString(): String {
        if (this == UNKNOWN) return "¿?"
        var string = name
        if (string.startsWith("G")) string = string.substring(1)
        if (string.endsWith("_PLUS")) string = string.substringBeforeLast("_PLUS") + '+'
        if (string.length == 1) string += 'º'
        return string.lowercase()
    }

    override fun toString(): String = asString()
}

enum class AidGrade : GradeValue {
    A0,
    A1, A1_PLUS,
    A2, A2_PLUS,
    A3, A3_PLUS,
    A4, A4_PLUS,
    A5, A5_PLUS,
    A_EQUIPPED;

    override fun asString(): String {
        var string = name
        if (string.endsWith("_PLUS")) string = string.substringBeforeLast("_PLUS") + '+'
        if (string.endsWith("_EQUIPPED")) string = string.substringBeforeLast("_EQUIPPED") + 'e'
        return string.uppercase()
    }

    override fun toString(): String = asString()
}
