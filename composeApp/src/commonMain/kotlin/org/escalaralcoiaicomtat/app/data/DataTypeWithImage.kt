package org.escalaralcoiaicomtat.app.data

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.network.BasicBackend
import kotlin.uuid.Uuid

@Serializable
sealed interface DataTypeWithImage : DataType {
    // Nullable to allow editing without uploading, must never be null
    val image: Uuid?

    fun imageUrl(width: Int? = null, height: Int? = null): String? {
        return image?.let { BasicBackend.downloadFileUrl(it, width, height) }?.toString()
    }

    fun copy(image: Uuid): DataTypeWithImage
}
