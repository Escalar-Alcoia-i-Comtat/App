package maps

import cache.ZipFile
import io.github.aakira.napier.Napier
import io.ktor.utils.io.readBuffer
import network.Backend
import platform.ZipFileHandler

object KMZHandler {

    private val hrefRegex = "<href>[\\w/.-]+</href>".toRegex()

    private fun replaceImages(zipFile: ZipFile) {
        var data = zipFile.read("doc.kml")!!.decodeToString()
        val matches = hrefRegex.findAll(data)
        for (match in matches) {
            val path = match.value.substring("<href>".length).substringBeforeLast("</href>")
            // Check if already replaced
            if (path.startsWith('/')) continue

            val file = zipFile.read(path)
            if (file != null) {
                data = data.replace(match.value, "<href>$file</href>")
                Napier.v { "Replaced icon $path" }
            } else {
                Napier.v { "Got a non existing icon (${path}): $file" }
            }
        }
        zipFile.write("doc.kml", data.encodeToByteArray())
    }

    /**
     * Downloads the KMZ stored in the given URL, decompresses it, and returns it in a way that can
     * be loaded by Maps UI engines.
     *
     * @return The KML file downloaded.
     */
    suspend fun load(
        uuid: String,
        replaceImagePaths: Boolean = true,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): ZipFile {
        Napier.d { "Downloading KMZ for $uuid ..." }
        val kmzFile = Backend.downloadFile(uuid, progress).readBuffer()
        Napier.d { "Unzipping KMZ into memory..." }
        val data = ZipFileHandler.unzip(kmzFile)
        if (replaceImagePaths) replaceImages(data)
        return data
    }
}
