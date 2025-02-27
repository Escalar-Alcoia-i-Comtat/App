package data

import kotlinx.serialization.Serializable
import network.BasicBackend
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@ExperimentalUuidApi
sealed interface DataTypeWithImage : DataType {
    // Nullable to allow editing without uploading, must never be null
    val image: Uuid?

    fun imageUrl(): String = BasicBackend.downloadFileUrl(image!!).toString()

    fun copy(image: Uuid): DataTypeWithImage
}
