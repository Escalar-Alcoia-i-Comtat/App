package org.escalaralcoiaicomtat.app.ui.reusable.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun RichTextEditor(
    markdownText: String?,
    onMarkdownTextChange: (String?) -> Unit,
    isEnabled: Boolean,
) {
    OutlinedTextField(
        value = markdownText ?: "",
        onValueChange = { text -> onMarkdownTextChange(text.takeIf { it.isNotBlank() }) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
    )
}
