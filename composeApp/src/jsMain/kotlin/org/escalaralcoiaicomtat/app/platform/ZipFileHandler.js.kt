package org.escalaralcoiaicomtat.app.platform

import io.github.aakira.napier.Napier
import kotlinx.io.Buffer
import org.escalaralcoiaicomtat.app.cache.ZipFile

actual object ZipFileHandler {
    /**
     * Extracts a zip file into memory.
     */
    actual suspend fun unzip(zipData: Buffer): ZipFile {
        Napier.w { "Trying to unzip a buffer of ${zipData.size}B into memory. UNSUPPORTED!" }
        TODO("Not yet implemented")
    }
}
