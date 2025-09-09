package org.escalaralcoiaicomtat.app.data.generic

import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.editable.EditableLatLng

@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        fun valueOf(value: String): LatLng {
            val lat = value.substringBefore(',').toDouble()
            val lon = value.substringAfter(',').toDouble()
            return LatLng(lat, lon)
        }
    }

    constructor(latLng: Pair<Double, Double>): this(latLng.first, latLng.second)

    override fun toString(): String {
        return "$latitude,$longitude"
    }

    fun editable(): EditableLatLng = EditableLatLng(latitude.toString(), longitude.toString())
}
