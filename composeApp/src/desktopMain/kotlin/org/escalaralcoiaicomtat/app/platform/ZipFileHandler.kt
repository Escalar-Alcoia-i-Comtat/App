package org.escalaralcoiaicomtat.app.platform

import io.github.aakira.napier.Napier
import kotlinx.io.Buffer
import org.escalaralcoiaicomtat.app.cache.ZipFile
import org.escalaralcoiaicomtat.app.utils.MemoryUnzipUtils

actual object ZipFileHandler {
    /**
     * Extracts a zip file into memory.
     */
    actual suspend fun unzip(zipData: Buffer): ZipFile {
        Napier.d { "Extracting zip into memory..." }
        return MemoryUnzipUtils.unzip(zipData).also { Napier.d { "Extraction complete!" } }
    }
}
