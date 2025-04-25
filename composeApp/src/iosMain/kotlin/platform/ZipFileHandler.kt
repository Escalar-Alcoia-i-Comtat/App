package platform

import com.oldguy.common.io.ZipFile
import io.github.aakira.napier.Napier
import org.escalaralcoiaicomtat.app.cache.File

actual object ZipFileHandler {
    /**
     * Extracts the zip file stored at [file] into the given directory ([dir]).
     */
    actual suspend fun unzip(file: File, dir: File) {
        Napier.d { "Extracting $file into $dir..." }

        ZipFile(com.oldguy.common.io.File(file.path)).use { zip ->
            zip.entries.also { Napier.d { "  There are ${it.size} entries." } }.forEach { entry ->
                zip.readEntry(entry) { entry, content, count, last ->
                    val zipFile = File(dir, entry.directory.name)
                    zipFile.parent.mkdirs()
                    var written = if (zipFile.exists()) zipFile.readAllBytes() else ByteArray(0)
                    for (i in 0 until count.toInt()) {
                        val byte = content[i]
                        written += byte
                    }
                    zipFile.write(written)
                    Napier.d { "  Written $count bytes into ${entry.directory.name}." }
                }
            }
        }
    }
}
