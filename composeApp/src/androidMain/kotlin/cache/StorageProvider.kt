package cache

import android.content.Context
import cache.Files.file

actual class StorageProvider(private val context: Context) {
    actual val cacheDirectory: File get() = context.cacheDir.file
}
