package org.escalaralcoiaicomtat.app.ui.navigation

import androidx.navigation.NavOptions

actual fun <T : Destination> buildNavOptions(
    singleTop: Boolean,
    popUpTo: T?
): NavOptions {
    return NavOptions.Builder()
        .setLaunchSingleTop(singleTop)
        .apply {
            popUpTo?.let { setPopUpTo(it, false) }
        }
        .build()
}