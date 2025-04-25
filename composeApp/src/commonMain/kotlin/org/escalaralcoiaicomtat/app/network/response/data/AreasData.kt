package org.escalaralcoiaicomtat.app.network.response.data

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.Area

@Serializable
data class AreasData(
    val areas: List<Area>
): DataResponseType
