package cache

actual class StorageProvider {
    private val home = java.io.File(System.getProperty("user.home"))

    actual val cacheDirectory: File = java.io.File(home, ".escalaralcoiaicomtat").let {
        File(it.absolutePath)
    }
}
