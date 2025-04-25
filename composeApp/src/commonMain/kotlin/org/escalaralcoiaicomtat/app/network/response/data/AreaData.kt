@file:UseSerializers(UuidSerializer::class)

package org.escalaralcoiaicomtat.app.network.response.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.data.serialization.UuidSerializer
import kotlin.uuid.Uuid

@Serializable
data class AreaData(
    val id: Long,
    val timestamp: Long,
    @SerialName("display_name") val displayName: String,
    val image: Uuid,
): DataResponseType {
    /**
     * Converts the response into an [Area].
     *
     * **[Area.zones] will be empty.**
     */
    fun asArea(): Area = Area(id, timestamp, displayName, image, emptyList())
}
