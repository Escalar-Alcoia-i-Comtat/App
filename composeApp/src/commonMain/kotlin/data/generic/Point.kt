package data.generic

import kotlinx.serialization.Serializable

@Serializable
data class Point(
    val icon: String,
    val location: LatLng,
    val label: String
)
