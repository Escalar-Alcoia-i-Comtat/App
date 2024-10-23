package cache

actual class StorageProvider {
    private val home = java.io.File(System.getProperty("user.home"))
    private val cache = java.io.File(home, ".cache")

    actual val cacheDirectory: File = java.io.File(cache, "escalaralcoiaicomtat").let {
        File(it.absolutePath)
    }
}
