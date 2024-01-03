package cache

import cache.File.Companion.asFile
import okio.FileSystem

actual class StorageProvider {
    private val temp = FileSystem.SYSTEM_TEMPORARY_DIRECTORY

    actual val cacheDirectory: File = (temp / ".escalaralcoiaicomtat").asFile
}
