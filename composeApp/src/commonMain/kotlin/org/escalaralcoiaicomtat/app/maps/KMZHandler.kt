package org.escalaralcoiaicomtat.app.maps

import io.github.aakira.napier.Napier
import io.ktor.utils.io.readBuffer
import org.escalaralcoiaicomtat.app.cache.File
import org.escalaralcoiaicomtat.app.cache.ZipFile
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.network.BasicBackend
import org.escalaralcoiaicomtat.app.platform.ZipFileHandler
import kotlin.uuid.Uuid

object KMZHandler {

    private val hrefRegex = "<href>[\\w/.-]+</href>".toRegex()

    private fun replaceImages(zipFile: ZipFile) {
        var data = zipFile.read("doc.kml")!!.decodeToString()
        val matches = hrefRegex.findAll(data)
        for (match in matches) {
            val path = match.value.substring("<href>".length).substringBeforeLast("</href>")
            // Check if already replaced
            if (path.startsWith("data:")) continue

            val fileData = zipFile.read(path)
            val dir = File(storageProvider.cacheDirectory, "map-icons").also { it.mkdirs() }
            val file = File(dir, path.replace("/", "_"))
            if (fileData != null) {
                if (!file.exists()) { file.write(fileData) }
                data = data.replace(match.value, "<href>${file.path}</href>")
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
        uuid: Uuid,
        replaceImagePaths: Boolean = true,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): ZipFile {
        Napier.d { "Downloading KMZ for $uuid ..." }
        val kmzFile = BasicBackend.downloadFile(uuid, progress = progress).readBuffer()
        Napier.d { "Unzipping KMZ into memory..." }
        val data = ZipFileHandler.unzip(kmzFile)
        if (replaceImagePaths) replaceImages(data)
        return data
    }
}
