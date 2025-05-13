package org.escalaralcoiaicomtat.app.data

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface DataType : Entity, Comparable<DataType> {
    @SerialName("display_name")
    val displayName: String

    override operator fun compareTo(other: DataType): Int

    fun refreshTimestamp() = copy(timestamp = Clock.System.now().toEpochMilliseconds())

    fun copy(displayName: String = this.displayName): DataType
}
