package cache

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import cache.Files.delete
import cache.Files.exists
import cache.Files.isDirectory
import cache.Files.listAllFiles
import cache.Files.mkdirs
import cache.Files.readAllBytes
import cache.Files.write
import network.response.data.FileRequestData
import image.decodeImage
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.toByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import network.Backend

object ImageCache {
    private val client = HttpClient()

    private val imageCacheDirectory: File by lazy {
        storageProvider.cacheDirectory + "images"
    }

    private val uuidRegex = Regex("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")

    init {
        Napier.i { "Image cache directory: $imageCacheDirectory" }
    }

    /**
     * Checks that the value provided in [fileRequest] matches the local data.
     * Downloads the file again if needed.
     *
     * @param fileRequest The response given by the server for the current data of the file.
     * @param enforceHttps If `true`, all `http://` requests will be converted into `https://`
     *
     * @return `null` if the file is up to date, the new file's data otherwise.
     */
    private suspend fun validateCachedFile(
        fileRequest: FileRequestData,
        enforceHttps: Boolean = true
    ): ByteArray? {
        val uuid = fileRequest.uuid
        val file = imageCacheDirectory + uuid
        val hashFile = File(file.path + "_hash")

        Napier.v(tag = "ImageCache-$uuid") { "Got file data" }
        if (!hashFile.exists() || hashFile.readAllBytes().decodeToString() != fileRequest.hash) {
            Napier.d(tag = "ImageCache-$uuid") {
                "Cached file not up to date. Deleting local copy and downloading again..."
            }

            // Download the file again
            if (file.exists()) file.delete()
            if (hashFile.exists()) hashFile.delete()

            var url = fileRequest.download
            if (enforceHttps && url.startsWith("http:")) {
                url = url.replace("http:", "https:")
            }

            val bytes = client.get(url).bodyAsChannel().toByteArray()
            file.write(bytes)
            hashFile.write(fileRequest.hash.encodeToByteArray())

            return bytes
        } else {
            Napier.v(tag = "ImageCache-$uuid") { "Cached file up to date" }
        }
        return null
    }

    /**
     * Fetches the data of all the files currently in cache, and updates them in case it's
     * necessary.
     */
    suspend fun updateCache() {
        if (!imageCacheDirectory.exists()) {
            // There isn't any file cached, just ignore
            Napier.i(tag = "ImageCache-updates") { "There isn't any cached image. Ignoring..." }
            return
        }

        val files = imageCacheDirectory.listAllFiles()
            // Exclude all directories
            ?.filter { !it.isDirectory }
            // Exclude all non-uuid
            ?.filter { uuidRegex.matches(it.name) }
            // Collect only the file names (uuid)
            ?.map { it.name }
        if (files == null) {
            Napier.w(tag = "ImageCache-updates") { "Cache directory doesn't support file listing." }
            return
        }
        Napier.d(tag = "ImageCache-updates") { "Got ${files.size} cached files." }
        val resultCount = Backend.requestFiles(files)
            .also { Napier.d(tag = "ImageCache-updates") { "Got response of ${it.size} files." } }
            .count { data -> validateCachedFile(data) != null }
        Napier.d(tag = "ImageCache-updates") { "Updated $resultCount cached files." }
    }

    @Composable
    fun collectStateOf(uuid: String): State<ImageBitmap?> {
        val state: MutableState<ImageBitmap?> = remember { mutableStateOf(null) }

        /**
         * Stores whether the image data has already been fetched from the server in this lifecycle.
         */
        var alreadyFetchedUpdate by remember { mutableStateOf(false) }

        LaunchedEffect(uuid) {
            if (!imageCacheDirectory.exists()) {
                require(imageCacheDirectory.mkdirs()) {
                    "Could not create cache directory ($imageCacheDirectory)."
                }
            }

            val file = imageCacheDirectory + uuid

            Napier.v(tag = "ImageCache-$uuid") { "$file" }

            CoroutineScope(Dispatchers.IO).launch {
                if (file.exists()) {
                    Napier.d(tag = "ImageCache-$uuid") { "Already cached, sending bytes..." }
                    val bytes = file.readAllBytes()
                    state.value = bytes.decodeImage()
                }

                if (!file.exists() || !alreadyFetchedUpdate) launch(Dispatchers.IO) {
                    try {
                        Napier.v(tag = "ImageCache-$uuid") { "Requesting file data..." }
                        val fileRequest = Backend.requestFile(uuid)
                        validateCachedFile(fileRequest)?.let { bytes ->
                            state.value = bytes.decodeImage()
                        }

                        alreadyFetchedUpdate = true
                    } catch (e: Exception) {
                        Napier.w(throwable = e, tag = "ImageCache-$uuid") {
                            "Could not get file metadata."
                        }
                    }
                }
            }
        }

        return state
    }
}
