package data

import kotlinx.serialization.Serializable
import network.BasicBackend
import kotlin.uuid.Uuid

@Serializable
sealed interface DataTypeWithGPX : DataType {
    val gpx: Uuid?

    fun gpxUrl(): String? = gpx?.let(BasicBackend::downloadFileUrl)?.toString()

    fun copyGpx(gpx: Uuid): DataTypeWithGPX
}
