package platform

import cache.File
import io.github.aakira.napier.Napier
import kotlinx.io.files.SystemFileSystem

actual object ZipFileHandler {
    /**
     * Extracts the zip file stored at [file] into the given directory ([dir]).
     */
    actual suspend fun unzip(file: File, dir: File) {
        Napier.d { "Extracting $file into $dir..." }
        val path = file.path.toPath()
        val zipFileSystem = SystemFileSystem.openZip(path)
        val files = zipFileSystem.listRecursively("/".toPath())
        Napier.d { "There are ${files.count()} files in the ZIP file." }
        for (zipPath in files) {
            // Ignore directories
            if (zipFileSystem.metadata(zipPath).isDirectory) continue

            val target = File(dir, zipPath.toString())
            Napier.v { "  Reading from #ZIP/$zipPath..." }
            val contents = zipFileSystem.source(zipPath).use { source ->
                source.buffer().use { buffer ->
                    buffer.readByteArray()
                }
            }
            Napier.v { "  Writing #ZIP/$zipPath into $target..." }
            target.parent.mkdirs()
            target.write(contents)
            Napier.v { "    Write OK!" }
        }
        Napier.d { "Extraction complete!" }
    }
}
