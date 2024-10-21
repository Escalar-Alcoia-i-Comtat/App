package maps

import cache.CacheContainer
import cache.File
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import network.Backend
import platform.ZipFileHandler

object KMZHandler : CacheContainer("kmz") {

    init {
        Napier.d { "Initializing KMZHandler..." }
    }

    /**
     * Requests the server for the updated info of the file with the given [uuid].
     * If the file is not downloaded, or a new update is available, it's downloaded.
     * TODO: Handle connection failures, for example, for when internet is not available.
     *
     * @param uuid The UUID of the file to request.
     * @param progress If desired, gives updates to the download progress.
     *
     * @return The contents of the KMZ file.
     */
    private suspend fun download(
        uuid: String,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): File {
        cacheDirectory.mkdirs()

        val file = File(cacheDirectory, uuid)
        val hashFile = File(file.path + "_hash")
        val request = Backend.requestFile(uuid, progress)

        Napier.d { "Downloading KMZ to $file" }

        // Make sure there are no situations when hash exists but the file doesn't or reverse
        if (hashFile.exists() && !file.exists()) hashFile.delete()
        if (!hashFile.exists() && file.exists()) file.delete()

        if (hashFile.exists()) {
            // The file is cached, let's see if the hash has been updated
            if (hashFile.readAllBytes().decodeToString() != request.hash) {
                // Hashes do not match, the file has to be updated
                // Delete the stored files
                file.delete()
                hashFile.delete()
            }
        }

        if (!file.exists()) {
            // Download the file
            val url = request.download.replace("http:", "https:")
            val response = client.get(url) {
                onDownload { bytesSentTotal, contentLength ->
                    contentLength ?: return@onDownload
                    progress?.invoke(bytesSentTotal, contentLength)
                }
            }
            if (response.status.value !in 200 until 300) {
                error("Server responded with a non-valid answer to $url")
            }
            val bytes = response.bodyAsBytes()
            file.write(bytes)
            hashFile.write(request.hash.encodeToByteArray())
        }

        return file
    }

    private val hrefRegex = "<href>[\\w/.-]+</href>".toRegex()

    private fun replaceImages(dataDir: File) {
        val kmlFile = File(dataDir, "doc.kml")
        var data = kmlFile.readAllBytes().decodeToString()
        val matches = hrefRegex.findAll(data)
        for (match in matches) {
            val path = match.value.substring("<href>".length).substringBeforeLast("</href>")
            // Check if already replaced
            if (path.startsWith('/')) continue

            val file = File(dataDir, path)
            if (file.exists()) {
                data = data.replace(match.value, "<href>$file</href>")
                Napier.v { "Replaced icon $path" }
            } else {
                Napier.v { "Got a non existing icon (${path}): $file" }
            }
        }
        kmlFile.delete()
        kmlFile.write(data.encodeToByteArray())
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
    ): File {
        val kmzFile = download(uuid, progress)
        val dataDir = File(cacheDirectory, "${uuid}_data")
        if (dataDir.exists()) dataDir.delete()
        ZipFileHandler.unzip(kmzFile, dataDir)
        if (replaceImagePaths) replaceImages(dataDir)
        return File(dataDir, "doc.kml")
    }
}
