package org.escalaralcoiaicomtat.app.network.response.data

import kotlinx.serialization.Serializable

@Serializable
data class ServerInfoResponseData(
    val version: String,
    val uuid: String,
    val databaseVersion: Int?,
    val lastUpdate: Long?,
): DataResponseType
