package cache

import io.github.aakira.napier.Napier
import network.createHttpClient

/**
 * A container for caching data.
 *
 * @param name The name of the cache container. If `\u0000` is used, caching will be blocked.
 */
abstract class CacheContainer(name: String) {
    protected val client = createHttpClient()

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
}
