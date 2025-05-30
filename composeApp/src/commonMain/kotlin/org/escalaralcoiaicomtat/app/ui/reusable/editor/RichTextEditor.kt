package org.escalaralcoiaicomtat.app.ui.reusable.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RichTextEditor(
    markdownText: String?,
    onMarkdownTextChange: (String?) -> Unit,
    isEnabled: Boolean,
) {
    val state = rememberRichTextState()
    LaunchedEffect(Unit) {
        markdownText?.let(state::setMarkdown)
    }
    LaunchedEffect(state.annotatedString) {
        onMarkdownTextChange(state.toMarkdown().takeUnless { it.isBlank() })
    }

    RichTextStyleRow(
        state = state,
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnabled,
    )
    OutlinedRichTextEditor(
        state = state,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
    )
}
