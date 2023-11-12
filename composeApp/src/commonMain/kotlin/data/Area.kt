package data

import data.model.DataTypeWithDisplayName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Area(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,
    val image: String,
    @SerialName("web_url") val webUrl: String,
    val zones: List<Zone>
): DataTypeWithDisplayName()
