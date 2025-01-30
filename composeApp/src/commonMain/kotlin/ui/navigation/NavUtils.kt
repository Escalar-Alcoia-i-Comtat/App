package ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun <T: Destination> NavController.navigateTo(destination: T) {
    navigate(
        destination,
        NavOptions.Builder().setLaunchSingleTop(true).build()
    )
}
