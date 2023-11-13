package data

import data.model.DataTypeWithImage
import database.Area
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
): DataTypeWithImage() {
    // TODO - load zones
    constructor(area: Area): this(area.id, area.timestamp, area.displayName, area.image, area.webUrl, emptyList())
}
