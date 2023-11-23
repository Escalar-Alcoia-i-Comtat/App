package network

import data.Area
import data.FileRequestData
import data.FilesRequestData
import exception.ServerException
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import network.response.DataResponse
import network.response.ErrorResponse

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

    @Serializable
    data class AreasData(
        val areas: List<Area>
    )

    /**
     * If the request was successful, extracts a [DataResponse] with type [DataType] from its body.
     * Otherwise extracts the error given, and throws it as a [ServerException].
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     */
    private suspend fun <DataType: Any> decodeBody(response: HttpResponse): DataType {
        val status = response.status.value

        return if (status in 200..299) {
            try {
                response.body<DataResponse<DataType>>().data
            } catch (_: NoTransformationFoundException) {
                throw response.body<ErrorResponse>().exception
            }
        } else {
            throw response.body<ErrorResponse>().exception
        }
    }

    /**
     * Makes a call to the `/tree` endpoint.
     *
     * @return A list of all the [Area]s provided by the server.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     */
    suspend fun tree(): List<Area> {
        val response = client.get(
            URLBuilder(baseUrl)
                .appendPathSegments("tree")
                .build()
        )
        return decodeBody<AreasData>(response).areas
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
     */
    suspend fun requestFile(uuid: String): FileRequestData {
        val response = client.get(
            URLBuilder(baseUrl)
                .appendPathSegments("file", uuid)
                .build()
        )
        return decodeBody(response)
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
     */
    suspend fun requestFiles(uuids: List<String>): List<FileRequestData> {
        val response = client.get(
            URLBuilder(baseUrl)
                .appendPathSegments("file", uuids.joinToString(","))
                .build()
        )
        return decodeBody(response)
    }
}
