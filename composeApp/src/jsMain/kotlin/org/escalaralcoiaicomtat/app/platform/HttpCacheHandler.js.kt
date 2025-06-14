package org.escalaralcoiaicomtat.app.platform

import io.ktor.client.plugins.cache.storage.CacheStorage

/**
 * Configures the cache for the HTTP client.
 * May throw [UnsupportedOperationException] if the current platform does not support file system
 * operations.
 */
actual fun httpCacheStorage(name: String): CacheStorage {
    throw UnsupportedOperationException("WASM does not support file system operations.")
}
