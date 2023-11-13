package cache

import cache.Files.file

actual class StorageProvider {
    private val home = java.io.File(System.getProperty("user.home"))

    actual val cacheDirectory: File = java.io.File(home, ".escalaralcoiaicomtat").file
}
