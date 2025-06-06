package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

interface GradeContainer {
    val grade: GradeValue?
    val aidGrade: GradeValue?

    @Composable
    fun grade(): AnnotatedString = buildAnnotatedString {
        grade?.toAnnotatedString()?.let { append(it) }
        if (grade != null && aidGrade != null) append('/')
        aidGrade?.toAnnotatedString()?.let { append(it) }
    }
}
