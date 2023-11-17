package data

import kotlinx.serialization.Serializable

@Serializable
data class FilesRequestData(
    val files: List<FileRequestData>
)
