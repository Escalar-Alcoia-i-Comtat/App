package data.generic

import kotlinx.serialization.Serializable

@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double
) {
    override fun toString(): String {
        return "$latitude,$longitude"
    }
}
