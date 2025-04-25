package cache

import okio.FileSystem
import org.escalaralcoiaicomtat.app.cache.File
import org.escalaralcoiaicomtat.app.cache.File.Companion.asFile

actual class StorageProvider {
    private val temp = FileSystem.SYSTEM_TEMPORARY_DIRECTORY

    actual val cacheDirectory: File = (temp / ".escalaralcoiaicomtat").asFile
}
