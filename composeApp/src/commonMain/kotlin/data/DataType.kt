package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface DataType: Comparable<DataType> {
    val id: Long
    val timestamp: Long

    @SerialName("display_name")
    val displayName: String

    override operator fun compareTo(other: DataType): Int
}
