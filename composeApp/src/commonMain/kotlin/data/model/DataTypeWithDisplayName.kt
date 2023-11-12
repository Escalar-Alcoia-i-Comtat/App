package data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class DataTypeWithDisplayName: DataType() {
    @SerialName("display_name")
    abstract val displayName: String
}
