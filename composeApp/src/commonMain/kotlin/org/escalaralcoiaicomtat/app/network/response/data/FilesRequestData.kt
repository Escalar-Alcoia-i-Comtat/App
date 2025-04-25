package org.escalaralcoiaicomtat.app.network.response.data

import kotlinx.serialization.Serializable

@Serializable
data class FilesRequestData(
    val files: List<FileRequestData>
): DataResponseType
