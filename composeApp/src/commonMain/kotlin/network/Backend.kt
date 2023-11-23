package network

import data.Area
import data.FileRequestData
import data.FilesRequestData
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

    private suspend fun <DataType: Any> decodeBody(response: HttpResponse): DataType {
        val status = response.status.value

        if (status in 200..299) {
            return try {
                response.body<DataResponse<DataType>>().data
            } catch (_: NoTransformationFoundException) {
                throw response.body<ErrorResponse>().exception
            }
        } else {
            throw response.body<ErrorResponse>().exception
        }
    }

    suspend fun tree(): List<Area> {
        val response = client.get(
            URLBuilder(baseUrl)
                .appendPathSegments("tree")
                .build()
        )
        return decodeBody<AreasData>(response).areas
    }

    suspend fun requestFile(uuid: String): FileRequestData {
        val response = client.get(
            URLBuilder(baseUrl)
                .appendPathSegments("file", uuid)
                .build()
        )
        return decodeBody(response)
    }

    suspend fun requestFiles(uuids: List<String>): List<FileRequestData> {
        val response = client.get(
            URLBuilder(baseUrl)
                .appendPathSegments("file", uuids.joinToString(","))
                .build()
        )
        return decodeBody(response)
    }
}
