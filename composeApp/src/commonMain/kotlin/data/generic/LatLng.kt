package data.generic

import data.editable.EditableLatLng
import kotlinx.serialization.Serializable

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

    override fun toString(): String {
        return "$latitude,$longitude"
    }

    fun editable(): EditableLatLng = EditableLatLng(latitude.toString(), longitude.toString())
}
