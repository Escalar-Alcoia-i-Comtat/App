package data

import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import kotlinx.serialization.Serializable
import network.Backend
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@ExperimentalUuidApi
sealed interface DataTypeWithImage : DataType {
    // Nullable to allow editing without uploading, must never be null
    val image: Uuid?

    fun imageUrl(): String {
        return URLBuilder(Backend.baseUrl)
            .appendPathSegments("download", image.toString())
            .buildString()
    }

    fun copy(image: Uuid): DataTypeWithImage
}
