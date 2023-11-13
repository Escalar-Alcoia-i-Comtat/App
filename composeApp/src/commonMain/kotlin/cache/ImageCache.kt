package cache

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cache.Files.delete
import cache.Files.exists
import cache.Files.mkdirs
import cache.Files.readAllBytes
import cache.Files.write
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

    @Composable
    fun collectStateOf(uuid: String): State<ByteArray?> {
        val state: MutableState<ByteArray?> = remember { mutableStateOf(null) }

        /**
         * Stores whether the image data has already been fetched from the server in this lifecycle.
         */
        var alreadyFetchedUpdate by remember { mutableStateOf(false) }

        LaunchedEffect(uuid) {
            if (!storageProvider.cacheDirectory.exists()) {
                require(storageProvider.cacheDirectory.mkdirs()) {
                    "Could not create cache directory (${storageProvider.cacheDirectory})."
                }
            }

            val file = storageProvider.cacheDirectory + uuid
            val hashFile = storageProvider.cacheDirectory + uuid + "_hash"

            Napier.v(tag = "ImageCache-$uuid") { "$file" }

            CoroutineScope(Dispatchers.IO).launch {
                if (file.exists()) {
                    Napier.d(tag = "ImageCache-$uuid") { "Already cached, sending bytes..." }
                    state.value = file.readAllBytes()
                }

                if (!file.exists() || !alreadyFetchedUpdate) launch(Dispatchers.IO) {
                    try {
                        Napier.v(tag = "ImageCache-$uuid") { "Requesting file data..." }
                        val fileRequest = Backend.requestFile(uuid)
                        if (!hashFile.exists() || hashFile.readAllBytes().decodeToString() != fileRequest.hash) {
                            // Download the file again
                            if (file.exists()) file.delete()
                            if (hashFile.exists()) hashFile.delete()

                            val bytes = client.get(fileRequest.download).bodyAsChannel().toByteArray()
                            file.write(bytes)
                            hashFile.write(fileRequest.hash.encodeToByteArray())

                            state.value = bytes
                        } else {
                            Napier.v(tag = "ImageCache-$uuid") { "Cached file up to date" }
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
