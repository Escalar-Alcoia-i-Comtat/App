package platform

import cache.File

expect object ZipFileHandler {
    /**
     * Extracts the zip file stored at [file] into the given directory ([dir]).
     */
    fun unzip(file: File, dir: File)
}
