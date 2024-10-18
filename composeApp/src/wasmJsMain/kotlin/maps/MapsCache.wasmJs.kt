package maps

import cache.File
import cache.storageProvider

actual object MapsCache {
    actual val tilesCacheDirectory: File? by lazy {
        storageProvider.cacheDirectory + "maps"
    }
}