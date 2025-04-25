package org.escalaralcoiaicomtat.app.platform

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun clipEntryOf(value: String): ClipEntry {
    return ClipEntry(ClipData.newPlainText(null, value))
}
