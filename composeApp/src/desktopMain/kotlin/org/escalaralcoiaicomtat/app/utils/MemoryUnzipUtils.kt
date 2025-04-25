package org.escalaralcoiaicomtat.app.utils

import io.ktor.utils.io.streams.inputStream
import kotlinx.io.Buffer
import org.escalaralcoiaicomtat.app.cache.ZipFile
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipInputStream

object MemoryUnzipUtils {
    fun unzip(zipData: Buffer): ZipFile {
        val output = ZipFile()

        ZipInputStream(zipData.inputStream()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    val bytes = ByteArrayOutputStream()
                    extractFile(zip, BufferedOutputStream(bytes))
                    output.write(entry.name, bytes.toByteArray())
                }
                entry = zip.nextEntry
            }
        }

        return output
    }

    private fun extractFile(inputStream: InputStream, outputStream: OutputStream) {
        outputStream.buffered().use { bos ->
            val bytesIn = ByteArray(BUFFER_SIZE)
            var read: Int
            while (inputStream.read(bytesIn).also { read = it } != -1) {
                bos.write(bytesIn, 0, read)
            }
        }
    }

    /**
     * Size of the buffer to read/write data
     */
    private const val BUFFER_SIZE = 4096

}
