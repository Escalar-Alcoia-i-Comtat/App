package data.model

import kotlinx.serialization.Serializable

@Serializable
abstract class DataType {
    abstract val id: Long
    abstract val timestamp: Long
}
