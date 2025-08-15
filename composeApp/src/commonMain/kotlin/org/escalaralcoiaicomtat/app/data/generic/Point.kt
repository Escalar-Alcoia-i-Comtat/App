package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Water
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import escalaralcoiaicomtat.composeapp.generated.resources.*
import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.data.editable.EditablePoint
import org.escalaralcoiaicomtat.app.data.serialization.PointNameSerializer
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Serializable
data class Point(
    @Serializable(with = PointNameSerializer::class) val icon: Name,
    val location: LatLng,
    val label: String,
    val description: String? = null,
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

        val label: StringResource get() = when(this) {
            DEFAULT -> Res.string.point_default
            PARKING -> Res.string.point_parking
            PARK -> Res.string.point_park
            WATER -> Res.string.point_water
            POOL -> Res.string.point_pool
            RESTAURANT -> Res.string.point_restaurant
            HOTEL -> Res.string.point_hotel
        }
    }

    fun editable(): EditablePoint = EditablePoint(icon, location.editable(), label, description ?: "")

    @Composable
    fun displayName(): String = label.takeIf { it.isNotBlank() } ?: stringResource(icon.label)
}
