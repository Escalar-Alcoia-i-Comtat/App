package platform

import cache.File

expect object ZipFileHandler {
    /**
     * Extracts the zip file stored at [file] into the given directory ([dir]).
     */
    suspend fun unzip(file: File, dir: File)
}
