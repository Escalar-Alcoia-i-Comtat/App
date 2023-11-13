package data.model

import kotlinx.serialization.Serializable

@Serializable
abstract class DataTypeWithImage: DataTypeWithDisplayName() {
    abstract val image: String
}
