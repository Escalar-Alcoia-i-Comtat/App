package ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import platform.onNavigate

fun <T: Destination> NavController.navigateTo(destination: T, isSingleTop: Boolean = false) {
    navigate(
        destination,
        NavOptions.Builder().setLaunchSingleTop(isSingleTop).build()
    )
    onNavigate(destination, isSingleTop)
}

fun NavController.navigateBack() {
    val previous = previousBackStackEntry
    if (previous == null) {
        // do nothing, nothing to go back to
    } else {
        val route = previous.toRoute<Destination>()
        onNavigate(route, false)
    }
}
