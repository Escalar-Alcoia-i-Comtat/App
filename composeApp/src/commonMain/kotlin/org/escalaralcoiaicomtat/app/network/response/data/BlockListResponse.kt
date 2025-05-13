package org.escalaralcoiaicomtat.app.network.response.data

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.Blocking

@Serializable
data class BlockListResponse(
    val blocks: List<Blocking>
): DataResponseType
