package network.response.data

import data.Area
import kotlinx.serialization.Serializable

@Serializable
data class AreasData(
    val areas: List<Area>
): DataResponseType
