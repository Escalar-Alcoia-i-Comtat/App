package platform

import androidx.compose.ui.platform.ClipEntry

actual fun clipEntryOf(value: String): ClipEntry {
    return ClipEntry(value)
}
