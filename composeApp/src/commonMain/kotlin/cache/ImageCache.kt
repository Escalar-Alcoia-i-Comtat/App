package cache

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import exception.UserLeftScreenException
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.Backend
import network.response.data.FileRequestData
import org.jetbrains.compose.resources.decodeToImageBitmap
import utils.IO

object ImageCache : CacheContainer("images") {

    private val uuidRegex = Regex("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")

    init {
        Napier.d { "Initializing ImageCache..." }
    }

    /**
     * If [enforceHttps] is `true`, and [url] starts with `http://`, it will be converted to
     * `https://`.
     */
    private fun asHttpsIfNeeded(enforceHttps: Boolean, url: String): String {
        return if (enforceHttps && url.startsWith("http:")) {
            url.replace("http:", "https:")
        } else {
            url
        }
    }

    /**
     * Checks that the value provided in [fileRequest] matches the local data.
     * Downloads the file again if needed.
     *
     * If [cacheSupported] is `false`, this function will always download the file again, and it
     * won't be stored anywhere.
     *
     * @param fileRequest The response given by the server for the current data of the file.
     * @param enforceHttps If `true`, all `http://` requests will be converted into `https://`
     *
     * @return `null` if the file is up to date, the new file's data otherwise.
     */
    private suspend fun validateCachedFile(
        fileRequest: FileRequestData,
        enforceHttps: Boolean = true,
        onProgressUpdate: (suspend (current: Long, max: Long?) -> Unit)? = null
    ): ByteArray? {
        if (!cacheSupported) {
            val url = asHttpsIfNeeded(enforceHttps, fileRequest.download)

            val bytes = client.get(url) {
                onDownload(onProgressUpdate)
            }.bodyAsBytes()
            return bytes
        }

        val uuid = fileRequest.uuid
        val file = cacheDirectory + uuid
        val hashFile = File(file.path + "_hash")

        Napier.v(tag = "ImageCache-$uuid") { "Got file data" }
        if (!hashFile.exists() || hashFile.readAllBytes().decodeToString() != fileRequest.hash) {
            Napier.d(tag = "ImageCache-$uuid") {
                "Cached file not up to date. Deleting local copy and downloading again..."
            }

            // Download the file again
            if (file.exists()) file.delete()
            if (hashFile.exists()) hashFile.delete()

            val url = asHttpsIfNeeded(enforceHttps, fileRequest.download)
            val bytes = client.get(url) {
                onDownload(onProgressUpdate)
            }.bodyAsBytes()
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
        if (!cacheSupported) {
            Napier.i(tag = "ImageCache-updates") { "Cache not supported. Ignoring..." }
            return
        }
        if (!cacheDirectory.exists()) {
            // There isn't any file cached, just ignore
            Napier.i(tag = "ImageCache-updates") { "There isn't any cached image. Ignoring..." }
            return
        }

        val files = cacheDirectory.listAllFiles()
            // Exclude all directories
            ?.filter { !it.isDirectory }
            // Exclude all non-uuid
            ?.filter { uuidRegex.matches(it.name) }
            // Filter empty uuids
            ?.filter { it.name.isNotBlank() }
            // Collect only the file names (uuid)
            ?.map { it.name }
        if (files == null) {
            Napier.w(tag = "ImageCache-updates") { "Cache directory doesn't support file listing." }
            return
        }
        Napier.d(tag = "ImageCache-updates") { "Got ${files.size} cached files." }
        // todo - not working
        /*val resultCount = Backend.requestFiles(files)
            .also { Napier.d(tag = "ImageCache-updates") { "Got response of ${it.size} files." } }
            .count { data -> validateCachedFile(data) != null }
        Napier.d(tag = "ImageCache-updates") { "Updated $resultCount cached files." }*/
    }

    @Composable
    fun collectStateOf(
        uuid: String,
        onProgressUpdate: (suspend (current: Long, max: Long?) -> Unit)? = null
    ): State<ImageBitmap?> {
        val state: MutableState<ImageBitmap?> = remember { mutableStateOf(null) }

        if (cacheSupported) {
            ObserveUUIDFile(uuid, state, onProgressUpdate)
        } else {
            FetchUUID(uuid, state, onProgressUpdate)
        }

        return state
    }

    @Composable
    private fun ObserveUUIDFile(
        uuid: String,
        state: MutableState<ImageBitmap?>,
        onProgressUpdate: (suspend (current: Long, max: Long?) -> Unit)? = null
    ) {
        /**
         * Stores whether the image data has already been fetched from the server in this lifecycle.
         */
        var alreadyFetchedUpdate by remember { mutableStateOf(false) }

        DisposableEffect(uuid) {
            if (!cacheDirectory.exists()) {
                cacheDirectory.mkdirs()
            }

            val file = cacheDirectory + uuid

            Napier.v(tag = "ImageCache-$uuid") { "$file" }

            val job = try {
                CoroutineScope(Dispatchers.IO).launch {
                    if (file.exists()) {
                        Napier.d(tag = "ImageCache-$uuid") { "Already cached, sending bytes..." }
                        val bytes = file.readAllBytes()
                        state.value = bytes.decodeToImageBitmap()
                    }

                    if (!file.exists() || !alreadyFetchedUpdate) launch(Dispatchers.IO) {
                        try {
                            Napier.v(tag = "ImageCache-$uuid") { "Requesting file data ($uuid)..." }
                            val fileRequest = Backend.requestFile(uuid, onProgressUpdate)
                            validateCachedFile(fileRequest, onProgressUpdate = onProgressUpdate)?.let { bytes ->
                                state.value = bytes.decodeToImageBitmap()
                            }

                            alreadyFetchedUpdate = true
                        } catch (e: Exception) {
                            Napier.w(throwable = e, tag = "ImageCache-$uuid") {
                                "Could not get file metadata."
                            }
                        }
                    }
                }
            } catch (_: CancellationException) {
                null
            }

            onDispose {
                job?.cancel(
                    UserLeftScreenException("Stopped loading image $uuid.")
                )
            }
        }
    }

    @Composable
    private fun FetchUUID(
        uuid: String,
        state: MutableState<ImageBitmap?>,
        onProgressUpdate: (suspend (current: Long, max: Long?) -> Unit)?
    ) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                try {
                    Napier.v(tag = "ImageCache-$uuid") { "Requesting file data ($uuid)..." }
                    val fileRequest = Backend.requestFile(uuid, onProgressUpdate)
                    val fileBytes = validateCachedFile(fileRequest, onProgressUpdate = onProgressUpdate)
                    state.value = fileBytes?.decodeToImageBitmap()
                } catch (e: Exception) {
                    Napier.w(throwable = e, tag = "ImageCache-$uuid") {
                        "Could not get file metadata."
                    }
                }
            }
        }
    }
}
