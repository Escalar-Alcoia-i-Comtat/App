package cache

import cache.File.Companion.asFile
import kotlinx.io.files.Path
import kotlinx.io.files.SystemTemporaryDirectory

actual class StorageProvider {
    actual val cacheDirectory: File
        get() = Path(SystemTemporaryDirectory, ".escalaralcoiaicomtat").asFile
}
