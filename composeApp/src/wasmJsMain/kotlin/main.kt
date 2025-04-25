import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cache.StorageProvider
import cache.storageProvider
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import kotlinx.browser.window
import ui.Locales
import ui.navigation.Destinations
import ui.navigation.navigateTo

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()

    val body = document.body ?: return

    ComposeViewport(body) {
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            val lang = window.navigator.language
            body.lang = Locales.valueOf(lang)
        }

        AppRoot(startDestination = null, navController = navController)

        LaunchedEffect(Unit) {
            val initRoute = window.location.hash
            val initDestination = Destinations.parse(initRoute)
            navController.navigateTo(initDestination, true)

            window.bindToNavigation(navController) { entry ->
                val route = entry.destination.route.orEmpty()
                when {
                    route.startsWith(Destinations.Root.serializer().descriptor.serialName) -> {
                        "#root"
                    }
                    route.startsWith(Destinations.Intro.serializer().descriptor.serialName) -> {
                        "#intro"
                    }
                    route.startsWith(Destinations.Area.serializer().descriptor.serialName) -> {
                        val args = entry.toRoute<Destinations.Area>()
                        "#${args.areaId}"
                    }
                    route.startsWith(Destinations.Zone.serializer().descriptor.serialName) -> {
                        val args = entry.toRoute<Destinations.Zone>()
                        "#${args.parentAreaId}/${args.zoneId}"
                    }
                    route.startsWith(Destinations.Sector.serializer().descriptor.serialName) -> {
                        val args = entry.toRoute<Destinations.Sector>()
                        "#${args.parentAreaId}/${args.parentZoneId}/${args.sectorId}${args.pathId?.let { "/$it" } ?: ""}"
                    }
                    route.startsWith(Destinations.Editor.serializer().descriptor.serialName) -> {
                        val args = entry.toRoute<Destinations.Editor>()
                        "#edit/${args.dataTypes}${args.id?.let { "/$it" } ?: "/new"}"
                    }
                    // Doesn't set a URL fragment for all other routes
                    else -> ""
                }
            }
        }
    }
}
