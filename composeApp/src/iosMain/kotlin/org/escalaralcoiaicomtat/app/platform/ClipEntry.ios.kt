package org.escalaralcoiaicomtat.app.platform

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

@OptIn(ExperimentalComposeUiApi::class)
actual fun clipEntryOf(value: String): ClipEntry {
    return ClipEntry.withPlainText(value)
}
