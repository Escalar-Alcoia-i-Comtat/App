package network

import data.Area
import data.Path
import data.Sector
import data.Zone
import exception.ServerException
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.utils.io.ByteReadChannel
import network.response.DataResponse
import network.response.data.AreaData
import network.response.data.AreasData
import network.response.data.FileListRequestData
import network.response.data.FileRequestData
import network.response.data.PathData
import network.response.data.SectorData
import network.response.data.ZoneData
import kotlin.uuid.Uuid

/**
 * Allows running requests to the application backend.
 */
object BasicBackend : Backend() {
    /**
     * Makes a call to the `/tree` endpoint.
     *
     * @param progress If not null, will be called with the progress of the request.
     *
     * @return A list of all the [Area]s provided by the server.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    suspend fun tree(
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): List<Area> {
        return get(AreasData.serializer(), "tree", progress = progress).areas
    }

    suspend fun area(
        id: Int,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): Area? {
        return getOrNull(AreaData.serializer(), "area", id, progress = progress)?.asArea()
    }

    suspend fun zone(
        id: Int,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): Zone? {
        return getOrNull(ZoneData.serializer(), "zone", id, progress = progress)?.asZone()
    }

    suspend fun sector(
        id: Int,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): Sector? {
        return getOrNull(SectorData.serializer(), "sector", id, progress = progress)?.asSector()
    }

    suspend fun path(
        id: Int,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): Path? {
        return getOrNull(PathData.serializer(), "path", id, progress = progress)?.asPath()
    }

    /**
     * Requests the data of a file with the given UUID.
     * An exception may be thrown if the file was not found in the server.
     *
     * @param uuid The UUID of the file to fetch.
     * @param progress If not null, will be called with the progress of the request.
     *
     * @return The data of the file requested.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     * @throws NoSuchElementException If the server did not provide the file requested.
     */
    @Deprecated("Do not request file, use downloads with conditional headers")
    suspend fun requestFile(
        uuid: String,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): FileRequestData {
        Napier.d { "Requesting file $uuid to server...." }
        return get(FileListRequestData.serializer(), "file", uuid, progress = progress)
            .files
            .first()
    }

    /**
     * Downloads a file with the given UUID.
     * An exception may be thrown if the file was not found in the server.
     * @param uuid The UUID of the file to fetch.
     * @param progress If not null, will be called with the progress of the request.
     * @return A channel with the data of the file requested.
     */
    suspend fun downloadFile(
        uuid: Uuid,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): ByteReadChannel {
        Napier.d { "Downloading file $uuid from server...." }
        var length: Long = 0
        progress?.invoke(0, length)
        val response = client.get(
            downloadFileUrl(uuid).also { Napier.v("GET :: $it") }
        ) {
            onDownload { bytesSentTotal, contentLength ->
                contentLength ?: return@onDownload
                length = contentLength
                progress?.invoke(bytesSentTotal, contentLength)
            }
        }
        progress?.invoke(length, length)
        return response.bodyAsChannel()
    }

    /**
     * Constructs the URL to access for downloading files from the backend.
     * @param uuid The file's identifier.
     */
    fun downloadFileUrl(uuid: Uuid): Url =
        URLBuilder(baseUrl)
            .appendPathSegments("download", uuid.toString())
            .build()
}
