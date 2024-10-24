package platform

import cache.ZipFile
import kotlinx.io.Buffer

expect object ZipFileHandler {
    /**
     * Extracts a zip file into memory.
     */
    suspend fun unzip(zipData: Buffer): ZipFile
}
