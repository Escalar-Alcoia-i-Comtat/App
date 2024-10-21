package platform

import cache.File
import io.github.aakira.napier.Napier
import kotlin.io.path.Path
import kotlinx.io.files.SystemFileSystem

actual object ZipFileHandler {
    /**
     * Extracts the zip file stored at [file] into the given directory ([dir]).
     */
    actual suspend fun unzip(file: File, dir: File) {
        if (!dir.exists()) dir.mkdirs()
        Napier.d { "Extracting $file into $dir..." }
        val path = Path(file.path)
        val zipFileSystem = SystemFileSystem.openZip(path)
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
