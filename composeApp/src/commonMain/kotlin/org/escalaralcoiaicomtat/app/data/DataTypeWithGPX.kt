package org.escalaralcoiaicomtat.app.data

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.network.BasicBackend
import kotlin.uuid.Uuid

@Serializable
sealed interface DataTypeWithGPX : DataType {
    val gpx: Uuid?

    fun gpxUrl(): String? = gpx?.let(BasicBackend::downloadFileUrl)?.toString()

    fun copyGpx(gpx: Uuid): DataTypeWithGPX
}
