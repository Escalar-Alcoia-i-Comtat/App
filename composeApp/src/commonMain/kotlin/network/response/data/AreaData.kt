package network.response.data

import data.Area
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AreaData(
    val id: Long,
    val timestamp: Long,
    @SerialName("display_name") val displayName: String,
    val image: String,
    @SerialName("web_url") val webUrl: String,
): DataResponseType {
    /**
     * Converts the response into an [Area].
     *
     * **[Area.zones] will be empty.**
     */
    fun asArea(): Area = Area(id, timestamp, displayName, image, webUrl, emptyList())
}
