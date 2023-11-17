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
import androidx.compose.ui.graphics.toComposeImageBitmap
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
import org.jetbrains.skia.Image

object ImageCache {
    private val client = HttpClient()

    private val imageCacheDirectory: File by lazy {
        storageProvider.cacheDirectory + "images"
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
            val hashFile = imageCacheDirectory + uuid + "_hash"

            Napier.v(tag = "ImageCache-$uuid") { "$file" }

            CoroutineScope(Dispatchers.IO).launch {
                if (file.exists()) {
                    Napier.d(tag = "ImageCache-$uuid") { "Already cached, sending bytes..." }
                    val bytes = file.readAllBytes()
                    state.value = Image.makeFromEncoded(bytes).toComposeImageBitmap()
                }

                if (!file.exists() || !alreadyFetchedUpdate) launch(Dispatchers.IO) {
                    try {
                        Napier.v(tag = "ImageCache-$uuid") { "Requesting file data..." }
                        val fileRequest = Backend.requestFile(uuid)
                        Napier.v(tag = "ImageCache-$uuid") { "Got file data" }
                        if (!hashFile.exists() || hashFile.readAllBytes().decodeToString() != fileRequest.hash) {
                            // Download the file again
                            if (file.exists()) file.delete()
                            if (hashFile.exists()) hashFile.delete()

                            val bytes = client.get(fileRequest.download).bodyAsChannel().toByteArray()
                            file.write(bytes)
                            hashFile.write(fileRequest.hash.encodeToByteArray())

                            state.value = Image.makeFromEncoded(bytes).toComposeImageBitmap()
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
