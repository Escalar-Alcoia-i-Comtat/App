package platform

import cache.File
import cache.fileSystem
import io.github.aakira.napier.Napier
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip

actual object ZipFileHandler {
    /**
     * Extracts the zip file stored at [file] into the given directory ([dir]).
     */
    actual suspend fun unzip(file: File, dir: File) {
        if (!dir.exists()) dir.mkdirs()
        Napier.d { "Extracting $file into $dir..." }
        val path = file.path.toPath()
        val zipFileSystem = fileSystem.openZip(path)
        for (zipPath in zipFileSystem.listRecursively("/".toPath())) {
            val target = File(dir, zipPath.toString())
            if (target.exists()) continue
            if (zipFileSystem.metadata(zipPath).isDirectory) {
                zipFileSystem.createDirectories(zipPath)
                continue
            }

            Napier.v { "  Reading #ZIP~$zipPath..." }
            val contents = zipFileSystem.source(zipPath).use { source ->
                source.buffer().use { buffer ->
                    buffer.readByteArray()
                }
            }
            Napier.v { "  Writing #ZIP/$zipPath into $target..." }
            target.parent.mkdirs()
            target.write(contents)
        }
        Napier.d { "Extraction complete!" }
    }
}
