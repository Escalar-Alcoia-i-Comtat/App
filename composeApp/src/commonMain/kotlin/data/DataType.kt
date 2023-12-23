package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface DataType {
    val id: Long
    val timestamp: Long

    @SerialName("display_name")
    val displayName: String
}
