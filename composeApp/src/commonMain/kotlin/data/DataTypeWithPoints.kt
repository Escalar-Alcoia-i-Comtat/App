package data

import data.generic.Point
import kotlinx.serialization.Serializable

@Serializable
sealed interface DataTypeWithPoints : DataType {
    val points: List<Point>
}
