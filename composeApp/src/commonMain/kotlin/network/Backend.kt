package network

import data.Area
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import network.response.DataResponse

/**
 * Allows running requests to the application backend.
 */
object Backend {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient {
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

    suspend fun tree(): List<Area> {
        val response = client.get(
            URLBuilder(baseUrl)
                .appendPathSegments("tree")
                .build()
        )
        val body = response.body<DataResponse<AreasData>>()
        return body.data.areas
    }
}
