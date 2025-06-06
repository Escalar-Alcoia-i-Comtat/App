package org.escalaralcoiaicomtat.app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import kotlinx.browser.window
import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.sync.SyncManager
import org.escalaralcoiaicomtat.app.ui.Locales
import org.escalaralcoiaicomtat.app.ui.navigation.NavigationControllerToWindowBinder
import org.escalaralcoiaicomtat.app.ui.navigation.navigateTo

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()

    SyncManager.schedule()

    val body = document.body ?: return

    ComposeViewport(body) {
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            val lang = window.navigator.language
            body.lang = Locales.valueOf(lang).key
        }

        AppRoot(startDestination = null, navController = navController)

        NavigationControllerToWindowBinder(
            initRoute = { window.location.hash },
            bindToNavigation = { getBackStackEntryRoute ->
                window.bindToNavigation(navController, getBackStackEntryRoute)
            },
            onNavigateTo = { destination, isSingleTop ->
                navController.navigateTo(destination, isSingleTop)
            },
        )
    }
}
