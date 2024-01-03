package platform

import cache.File

actual object ZipFileHandler {
    /**
     * Extracts the zip file stored at [file] into the given directory ([dir]).
     */
    actual fun unzip(file: File, dir: File) {
        TODO("ZIP file extraction")
    }
}
