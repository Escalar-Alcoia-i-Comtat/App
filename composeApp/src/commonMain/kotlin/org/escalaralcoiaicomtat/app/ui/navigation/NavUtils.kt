package org.escalaralcoiaicomtat.app.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun <T: Destination> NavController.navigateTo(
    destination: T,
    singleTop: Boolean = false,
    popUpTo: T? = null
) {
    navigate(
        destination,
        NavOptions.Builder()
            .setLaunchSingleTop(singleTop)
            .apply {
                popUpTo?.let { setPopUpTo(it, false) }
            }
            .build()
    )
}
