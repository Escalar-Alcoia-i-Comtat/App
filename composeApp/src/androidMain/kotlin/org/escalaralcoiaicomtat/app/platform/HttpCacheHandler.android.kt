package org.escalaralcoiaicomtat.app.platform

import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.cache.storage.FileStorage
import org.escalaralcoiaicomtat.app.cache.storageProvider
import utils.asJavaFile

/**
 * Configures the cache for the HTTP client.
 * May throw [UnsupportedOperationException] if the current platform does not support file system
 * operations.
 */
actual fun httpCacheStorage(name: String): CacheStorage {
    val cacheFile = storageProvider.cacheDirectory + name
    return FileStorage(cacheFile.asJavaFile)
}
