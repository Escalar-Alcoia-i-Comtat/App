package org.escalaralcoiaicomtat.app.data

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.network.BasicBackend
import kotlin.uuid.Uuid

@Serializable
sealed interface DataTypeWithKMZ : DataType {
    // Nullable to allow editing without uploading, must never be null
    val kmz: Uuid?

    fun kmzUrl(): String = BasicBackend.downloadFileUrl(kmz!!).toString()

    fun copyKmz(kmz: Uuid): DataTypeWithKMZ
}
