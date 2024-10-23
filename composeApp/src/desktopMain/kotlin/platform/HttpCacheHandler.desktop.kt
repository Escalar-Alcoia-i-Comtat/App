package platform

import cache.storageProvider
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.cache.storage.FileStorage
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
