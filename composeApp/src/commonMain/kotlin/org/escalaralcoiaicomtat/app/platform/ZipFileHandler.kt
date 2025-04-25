package org.escalaralcoiaicomtat.app.platform

import kotlinx.io.Buffer
import org.escalaralcoiaicomtat.app.cache.ZipFile

expect object ZipFileHandler {
    /**
     * Extracts a zip file into memory.
     */
    suspend fun unzip(zipData: Buffer): ZipFile
}
