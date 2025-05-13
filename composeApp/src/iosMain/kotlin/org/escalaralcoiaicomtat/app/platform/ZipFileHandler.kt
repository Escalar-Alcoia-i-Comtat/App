package org.escalaralcoiaicomtat.app.platform

import com.oldguy.common.io.ZipFile
import io.github.aakira.napier.Napier
import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import org.escalaralcoiaicomtat.app.cache.File.Companion.asFile
import kotlin.uuid.Uuid
import com.oldguy.common.io.File as KMPFile
import org.escalaralcoiaicomtat.app.cache.ZipFile as AppZipFile

actual object ZipFileHandler {
    /**
     * Extracts a zip file into memory.
     */
    actual suspend fun unzip(zipData: Buffer): AppZipFile {
        val file = Path(SystemTemporaryDirectory, Uuid.random().toString())
        val sink = SystemFileSystem.sink(file)
        sink.buffered().use { it.write(zipData, zipData.size) }

        val output = AppZipFile()
        try {
            ZipFile(KMPFile(file.asFile.path)).use { zip ->
                zip.entries.also { Napier.d { "  There are ${it.size} entries." } }.forEach { entry ->
                    zip.readEntry(entry) { entry, content, count, last ->
                        output.append(entry.name, content)
                        Napier.d { "  Written $count bytes into ${entry.directory.name}." }
                    }
                }
            }
        } finally {
            SystemFileSystem.delete(file, false)
        }
        return output
    }
}
