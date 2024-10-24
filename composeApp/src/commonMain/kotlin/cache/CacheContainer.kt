package cache

import io.github.aakira.napier.Napier
import io.ktor.client.plugins.cache.HttpCache
import network.createHttpClient
import platform.httpCacheStorage

/**
 * A container for caching data.
 *
 * @param name The name of the cache container. If `\u0000` is used, caching will be blocked.
 */
@Deprecated("Use ktor cache")
abstract class CacheContainer(private val name: String) {
    protected val client by lazy {
        createHttpClient {
            install(HttpCache) {
                configureHttpClientCache()
            }
        }
    }

    /**
     * The directory where the cache will be stored.
     *
     * @throws UnsupportedOperationException If the current platform does not support file system operations.
     */
    val cacheDirectory: File by lazy { storageProvider.cacheDirectory + name }

    val cacheSupported: Boolean = try {
        if (name == "\u0000") {
            Napier.i(tag = this::class.simpleName) { "Caching is disabled." }
            false
        } else {
            Napier.i(tag = this::class.simpleName) { "Cache directory: $cacheDirectory" }
            true
        }
    } catch (_: UnsupportedOperationException) {
        Napier.w(tag = this::class.simpleName) { "FileSystem is not supported in the current platform." }
        false
    } catch (_: NullPointerException) {
        Napier.w(tag = this::class.simpleName) { "FileSystem is not available in the current platform." }
        false
    }

    /**
     * Configures the cache for the HTTP client.
     * If [cacheSupported] is `true`, [cacheDirectory] will be used for cache.
     * Otherwise an in-memory cache will be used.
     */
    private fun HttpCache.Config.configureHttpClientCache() {
        val storage = if (cacheSupported) {
            try {
                httpCacheStorage(name)
            } catch (_: UnsupportedOperationException) {
                null
            }
        } else {
            null
        }
        if (storage != null) {
            publicStorage(storage)
        }
    }
}
