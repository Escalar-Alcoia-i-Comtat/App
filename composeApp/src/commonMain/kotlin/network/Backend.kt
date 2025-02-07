package network

import build.BuildKonfig
import data.Area
import exception.ServerException
import io.github.aakira.napier.Napier
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charsets
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import network.response.DataResponse
import network.response.ErrorResponse
import network.response.data.AreasData
import network.response.data.DataResponseType
import network.response.data.FileListRequestData
import network.response.data.FileRequestData
import platform.httpCacheStorage

/**
 * Allows running requests to the application backend.
 */
object Backend {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = createHttpClient {
        install(ContentNegotiation) {
            json(
                json = json
            )
        }
        install(HttpCache) {
            val storage = try {
                httpCacheStorage("backend")
            } catch (_: UnsupportedOperationException) {
                null
            }
            if (storage != null) {
                publicStorage(storage)
            }
        }
    }

    private val baseUrl = BuildKonfig.BASE_URL ?: "https://backend.escalaralcoiaicomtat.org"

    init {
        Napier.i { "Base URL: $baseUrl" }
    }

    /**
     * If the request was successful, extracts a [DataResponse] with type [DataType] from its body.
     * Otherwise extracts the error given, and throws it as a [ServerException].
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    private suspend fun <DataType : DataResponseType> decodeBody(
        response: HttpResponse,
        deserializer: DeserializationStrategy<DataType>,
    ): DataType {
        return try {
            val url = response.request.url
            val status = response.status.value
            val body = response.bodyAsText(fallbackCharset = Charsets.UTF_8)

            /*Napier.v(tag = "Backend") {
                "Got response from server ($url - $status). Raw body: $body"
            }*/

            if (status in 200..299) {
                DataResponse.decode(body, deserializer).also {
                    Napier.d(tag = "Backend") {
                        "Server responded successfully to ${response.request.url}. Status: $status"
                    }
                }
            } else {
                Napier.e(tag = "Backend") {
                    "Server responded with an exception.\nUrl: $url\nCode: $status. Body: $body"
                }
                json.decodeFromString<ErrorResponse>(body).throwException(url)
            }
        } catch (exception: NoTransformationFoundException) {
            Napier.e(tag = "Backend", throwable = exception) {
                "Got unexpected response from server. It cannot be parsed."
            }
            error("Received an unhandleable response from the server.")
        } catch (exception: Exception) {
            Napier.e(tag = "Backend", throwable = exception) {
                "Could not decode server's body. Unknown exception."
            }
            throw exception
        }
    }

    /**
     * Runs an HTTP GET request to the backend server, at the given path.
     *
     * @param pathComponents The components of the path to request.
     * @param progress If not null, will be called with the progress of the request.
     *
     * @return The response given by the server, of the type desired.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    private suspend fun <DataType : DataResponseType> get(
        deserializer: DeserializationStrategy<DataType>,
        vararg pathComponents: String,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): DataType {
        var length: Long = 0
        progress?.invoke(0, length)
        val response = client.get(
            URLBuilder(baseUrl)
                .appendPathSegments(*pathComponents)
                .build()
                .also { Napier.v("GET :: $it") }
        ) {
            onDownload { bytesSentTotal, contentLength ->
                contentLength ?: return@onDownload
                length = contentLength
                progress?.invoke(bytesSentTotal, contentLength)
            }
        }
        progress?.invoke(length, length)
        return decodeBody(response, deserializer)
    }

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
        uuid: String,
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
    fun downloadFileUrl(uuid: String): Url =
        URLBuilder(baseUrl)
            .appendPathSegments("download", uuid)
            .build()
}
