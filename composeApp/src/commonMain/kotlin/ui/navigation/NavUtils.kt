package ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun <T: Destination> NavController.navigateTo(destination: T, singleTop: Boolean = false) {
    navigate(
        destination,
        NavOptions.Builder().setLaunchSingleTop(singleTop).build()
    )
}
