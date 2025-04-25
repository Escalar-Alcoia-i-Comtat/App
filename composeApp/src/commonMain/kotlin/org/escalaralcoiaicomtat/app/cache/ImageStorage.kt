package org.escalaralcoiaicomtat.app.cache

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import io.github.aakira.napier.Napier
import io.ktor.utils.io.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.escalaralcoiaicomtat.app.network.BasicBackend
import org.escalaralcoiaicomtat.app.utils.IO
import org.jetbrains.compose.resources.decodeToImageBitmap
import kotlin.uuid.Uuid

@Deprecated("Use Coil instead")
object ImageStorage {

    @Composable
    fun collectStateOf(
        uuid: Uuid,
        onProgressUpdate: (suspend (current: Long, max: Long?) -> Unit)? = null
    ): State<ImageBitmap?> {
        val state: MutableState<ImageBitmap?> = remember { mutableStateOf(null) }

        FetchUUID(uuid, state, onProgressUpdate)

        return state
    }

    @Composable
    private fun FetchUUID(
        uuid: Uuid,
        state: MutableState<ImageBitmap?>,
        onProgressUpdate: (suspend (current: Long, max: Long?) -> Unit)?
    ) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                try {
                    Napier.v(tag = "ImageCache-$uuid") { "Requesting file data ($uuid)..." }
                    val downloadChannel = BasicBackend.downloadFile(uuid, onProgressUpdate)
                    val fileBytes = downloadChannel.toByteArray()
                    state.value = fileBytes.decodeToImageBitmap()
                } catch (e: Exception) {
                    Napier.w(throwable = e, tag = "ImageCache-$uuid") {
                        "Could not get file metadata."
                    }
                }
            }
        }
    }
}
