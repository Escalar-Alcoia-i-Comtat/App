package data

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface DataType : Comparable<DataType> {
    val id: Long
    val timestamp: Long

    @SerialName("display_name")
    val displayName: String

    override operator fun compareTo(other: DataType): Int

    fun refreshTimestamp() = copy(timestamp = Clock.System.now().toEpochMilliseconds())

    fun copy(
        id: Long = this.id,
        timestamp: Long = this.timestamp,
        displayName: String = this.displayName
    ): DataType
}
