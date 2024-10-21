package cache

actual class StorageProvider {
    actual val cacheDirectory: File get() = throw UnsupportedOperationException("Not supported on WASM")
}
