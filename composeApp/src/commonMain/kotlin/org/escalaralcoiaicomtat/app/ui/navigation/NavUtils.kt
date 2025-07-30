package org.escalaralcoiaicomtat.app.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

expect fun <T: Destination> buildNavOptions(
    singleTop: Boolean = false,
    popUpTo: T? = null
): NavOptions

fun <T: Destination> NavController.navigateTo(
    destination: T,
    singleTop: Boolean = false,
    popUpTo: T? = null
) {
    navigate(destination, buildNavOptions(singleTop, popUpTo))
}
