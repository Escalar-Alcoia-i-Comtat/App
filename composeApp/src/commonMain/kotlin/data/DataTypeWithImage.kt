package data

import build.BuildKonfig
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import kotlinx.serialization.Serializable
import network.Backend.BASE_URL_FALLBACK
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@ExperimentalUuidApi
sealed interface DataTypeWithImage : DataType {
    // Nullable to allow editing without uploading, must never be null
    val image: Uuid?

    fun imageUrl(): String {
        return URLBuilder(BuildKonfig.BASE_URL ?: BASE_URL_FALLBACK)
            .appendPathSegments("download", image.toString())
            .buildString()
    }

    fun copy(image: Uuid): DataTypeWithImage
}
