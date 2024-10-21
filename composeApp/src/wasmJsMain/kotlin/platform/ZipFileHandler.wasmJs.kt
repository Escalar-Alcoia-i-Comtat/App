package platform

import cache.File
import io.github.aakira.napier.Napier

actual object ZipFileHandler {
    /**
     * Extracts the zip file stored at [file] into the given directory ([dir]).
     */
    actual suspend fun unzip(file: File, dir: File) {
        Napier.w { "Trying to unzip $file into $dir. UNSUPPORTED!" }
        TODO("Not yet implemented")
    }
}
