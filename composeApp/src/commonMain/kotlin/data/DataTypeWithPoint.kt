package data

import data.generic.LatLng
import kotlinx.serialization.Serializable

@Serializable
sealed interface DataTypeWithPoint : DataType {
    val point: LatLng?
}
