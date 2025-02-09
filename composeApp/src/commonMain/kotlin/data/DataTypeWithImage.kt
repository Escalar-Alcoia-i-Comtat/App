package data

import build.BuildKonfig
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import kotlinx.serialization.Serializable
import network.Backend.BASE_URL_FALLBACK

@Serializable
sealed interface DataTypeWithImage : DataType {
    val image: String

    fun imageUrl(): String {
        return URLBuilder(BuildKonfig.BASE_URL ?: BASE_URL_FALLBACK)
            .appendPathSegments("download", image)
            .buildString()
    }

    fun copy(image: String): DataTypeWithImage
}
