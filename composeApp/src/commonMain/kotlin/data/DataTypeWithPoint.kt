package data

import data.generic.LatLng
import kotlinx.serialization.Serializable

@Serializable
sealed interface DataTypeWithPoint : DataType {
    val point: LatLng?

    /**
     * Checks whether the zone has any metadata to display.
     * @return `true` if [point] is not null.
     */
    fun hasAnyMetadata(): Boolean {
        return point != null
    }

    fun copy(point: LatLng?): DataTypeWithPoint
}
