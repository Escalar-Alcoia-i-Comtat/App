package org.escalaralcoiaicomtat.app.data

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.generic.Point

@Serializable
sealed interface DataTypeWithPoints : DataType {
    val points: List<Point>

    fun copy(points: List<Point>): DataTypeWithPoints
}
