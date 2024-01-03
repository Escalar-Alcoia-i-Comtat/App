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
    actual fun unzip(file: File, dir: File) {
        Napier.d { "Extracting $file into $dir..." }
        val path = file.path.toPath()
        val zipFileSystem = fileSystem.openZip(path)
        for (zipPath in zipFileSystem.listRecursively("/".toPath())) {
            val target = File(dir, zipPath.toString())
            val contents = zipFileSystem.source(zipPath).use { source ->
                source.buffer().use { buffer ->
                    buffer.readByteArray()
                }
            }
            Napier.v { " Writing #ZIP/$zipPath into $target..." }
            target.write(contents)
        }
        Napier.d { "Extraction complete!" }
    }
}
