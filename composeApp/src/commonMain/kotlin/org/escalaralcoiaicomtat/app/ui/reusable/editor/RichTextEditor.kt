package org.escalaralcoiaicomtat.app.ui.reusable.editor

import androidx.compose.runtime.Composable

@Composable
expect fun RichTextEditor(
    markdownText: String?,
    onMarkdownTextChange: (String?) -> Unit,
    isEnabled: Boolean = true
)
