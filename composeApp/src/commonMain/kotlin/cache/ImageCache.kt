package cache

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import network.Backend

object ImageCache {
    private val client = HttpClient()

    fun get(uuid: String): Flow<ByteArray> {
        if (!storageProvider.cacheDirectory.exists()) {
            require(storageProvider.cacheDirectory.mkdirs()) {
                "Could not create cache directory (${storageProvider.cacheDirectory})."
            }
        }

        val file = storageProvider.cacheDirectory + uuid
        val hashFile = storageProvider.cacheDirectory + uuid + "_hash"

        Napier.v(tag = "ImageCache-$uuid") { "$file" }

        return channelFlow {
            if (file.exists()) {
                Napier.d(tag = "ImageCache-$uuid") { "Already cached, sending bytes..." }
                send(file.readAllBytes())
            }

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

                    send(bytes)
                } else {
                    Napier.v(tag = "ImageCache-$uuid") { "Cached file up to date" }
                }
            } catch (e: Exception) {
                Napier.w(throwable = e, tag = "ImageCache-$uuid") {
                    "Could not get file metadata."
                }
            }
        }
    }
}
