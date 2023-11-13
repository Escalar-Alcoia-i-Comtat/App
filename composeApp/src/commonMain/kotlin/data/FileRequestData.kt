package data

import kotlinx.serialization.Serializable

@Serializable
data class FileRequestData(
    val hash: String,
    val filename: String,
    val download: String,
    val size: Long
)
