package org.escalaralcoiaicomtat.app.data

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.generic.LatLng

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
