package network

import data.Area
import network.response.data.FileRequestData
import exception.ServerException
import io.github.aakira.napier.Napier
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.charsets.Charsets
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import network.response.DataResponse
import network.response.data.DataResponseType
import network.response.ErrorResponse
import network.response.data.AreasData

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
    }

    private const val baseUrl = "https://backend.escalaralcoiaicomtat.org"

    /**
     * If the request was successful, extracts a [DataResponse] with type [DataType] from its body.
     * Otherwise extracts the error given, and throws it as a [ServerException].
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    private suspend fun <DataType: DataResponseType> decodeBody(response: HttpResponse): DataType {
        return try {
            val status = response.status.value
            val body = response.bodyAsText(fallbackCharset = Charsets.UTF_8)

            Napier.v(tag = "Backend") { "Got response from server ($status). Raw body: $body" }
            if (status in 200..299) {
                json.decodeFromString<DataResponse<DataType>>(body).data.also {
                    Napier.d(tag = "Backend") {
                        "Server responded successfully to ${response.request.url}. Status: $status"
                    }
                }
            } else {
                Napier.e(tag = "Backend") {
                    "Server responded with an exception. Code: $status. Body: $body"
                }
                throw json.decodeFromString<ErrorResponse>(body).exception
            }
        } catch (exception: NoTransformationFoundException) {
            Napier.e(tag = "Backend", throwable = exception) {
                "Got unexpected response from server. It cannot be parsed."
            }
            throw IllegalStateException("Received an unhandleable response from the server.")
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
     *
     * @return The response given by the server, of the type desired.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    private suspend fun <DataType: DataResponseType> get(vararg pathComponents: String): DataType {
        val response = client.get(
            URLBuilder(baseUrl)
                .appendPathSegments(*pathComponents)
                .build()
        )
        return decodeBody(response)
    }

    /**
     * Makes a call to the `/tree` endpoint.
     *
     * @return A list of all the [Area]s provided by the server.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    suspend fun tree(): List<Area> {
        return get<AreasData>("tree").areas
    }

    /**
     * Requests the data of a file with the given UUID.
     * An exception may be thrown if the file was not found in the server.
     *
     * @param uuid The UUID of the file to fetch.
     *
     * @return The data of the file requested.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    suspend fun requestFile(uuid: String): FileRequestData {
        return get("file", uuid)
    }

    /**
     * Requests the data of multiple files with the given UUIDs.
     * An exception may be thrown if at least one file was not found in the server.
     *
     * @param uuids The UUIDs of the files to fetch.
     *
     * @return The data of the file requested.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    suspend fun requestFiles(uuids: List<String>): List<FileRequestData> {
        return get("file", uuids.joinToString(","))
    }
}
