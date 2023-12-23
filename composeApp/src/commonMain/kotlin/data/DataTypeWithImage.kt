package data

import kotlinx.serialization.Serializable

@Serializable
sealed interface DataTypeWithImage : DataType {
    val image: String
}
