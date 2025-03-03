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
import data.editable.EditablePoint
import kotlinx.serialization.Serializable

@Serializable
data class Point(
    val icon: Name,
    val location: LatLng,
    val label: String
) {
    enum class Name {
        DEFAULT, PARKING, PARK, WATER, POOL, RESTAURANT, HOTEL;

        val key: String = name.lowercase()

        val iconVector: ImageVector get() = when (this) {
            DEFAULT -> Icons.Outlined.Place
            PARKING -> Icons.Outlined.LocalParking
            PARK -> Icons.Outlined.Park
            WATER -> Icons.Outlined.Water
            POOL -> Icons.Outlined.Pool
            RESTAURANT -> Icons.Outlined.Restaurant
            HOTEL -> Icons.Outlined.Hotel
        }
    }

    fun editable(): EditablePoint = EditablePoint(icon, location.editable(), label)
}
