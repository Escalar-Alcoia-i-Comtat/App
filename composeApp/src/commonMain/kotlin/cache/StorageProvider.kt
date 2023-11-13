package cache

lateinit var storageProvider: StorageProvider

expect class StorageProvider {
    val cacheDirectory: File
}
