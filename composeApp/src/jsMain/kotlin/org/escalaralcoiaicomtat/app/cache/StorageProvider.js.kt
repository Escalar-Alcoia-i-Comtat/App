package org.escalaralcoiaicomtat.app.cache

actual class StorageProvider {
    actual val cacheDirectory: File
        get() = throw UnsupportedOperationException("StorageProvider :: Not supported on JS")
}
