package org.escalaralcoiaicomtat.app.network.response.data

import kotlinx.serialization.Serializable

@Serializable
data class FileListRequestData(
    val files: List<FileRequestData>
): DataResponseType
