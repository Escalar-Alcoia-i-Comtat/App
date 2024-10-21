package platform

import cache.File
import io.github.aakira.napier.Napier
import utils.UnzipUtils
import utils.asJavaFile

actual object ZipFileHandler {
    /**
     * Extracts the zip file stored at [file] into the given directory ([dir]).
     */
    actual suspend fun unzip(file: File, dir: File) {
        Napier.d { "Extracting $file into $dir..." }
        UnzipUtils.unzip(file.asJavaFile, dir.asJavaFile)
        Napier.d { "Extraction complete!" }
    }
}
