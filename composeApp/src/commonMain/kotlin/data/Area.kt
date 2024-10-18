package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Area(
    override val id: Long,
    override val timestamp: Long,
    @SerialName("display_name") override val displayName: String,
    override val image: String,
    @SerialName("web_url") val webUrl: String,
    val zones: List<Zone>
): DataTypeWithImage {
    override fun compareTo(other: DataType): Int {
        // Sort by displayName
        return displayName.compareTo(other.displayName)
    }
}
