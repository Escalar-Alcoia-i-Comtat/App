package data.generic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Water
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Point(
    val icon: String,
    val location: LatLng,
    val label: String
) {
    companion object {
        const val POINT_NAME_DEFAULT = "default"
        const val POINT_NAME_PARKING = "parking"
        const val POINT_NAME_PARK = "park"
        const val POINT_NAME_WATER = "water"
        const val POINT_NAME_POOL = "pool"
        const val POINT_NAME_RESTAURANT = "restaurant"
        const val POINT_NAME_HOTEL = "hotel"
    }

    @Transient
    val iconVector: ImageVector = when (icon) {
        POINT_NAME_DEFAULT -> Icons.Outlined.Place
        POINT_NAME_PARKING -> Icons.Outlined.LocalParking
        POINT_NAME_PARK -> Icons.Outlined.Park
        POINT_NAME_WATER -> Icons.Outlined.Water
        POINT_NAME_POOL -> Icons.Outlined.Pool
        POINT_NAME_RESTAURANT -> Icons.Outlined.Restaurant
        POINT_NAME_HOTEL -> Icons.Outlined.Hotel
        else -> Icons.Outlined.Place
    }
}
