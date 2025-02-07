import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import cache.StorageProvider
import cache.storageProvider
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams
import ui.Locales
import ui.navigation.Destination
import ui.navigation.Destinations

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()

    val startDestination = calculateStartDestination()

    ComposeViewport(document.body!!) {
        LaunchedEffect(Unit) {
            val lang = window.navigator.language
            document.body?.lang = Locales.valueOf(lang)
        }

        AppRoot(startDestination = startDestination)
    }
}

/**
 * Obtains the start destination from the current URL. Format:
 * ```
 * https://domain.tld/{areaId?}/{zoneId?}/{sectorId?}?path={pathId?}
 * ```
 */
private fun calculateStartDestination(): Destination? {
    val pathPieces = window.location.pathname
        .split('/')
        .filterNot(String::isBlank)
        .mapNotNull(String::toLongOrNull)

    val query = URLSearchParams(window.location.search.toJsString())
    val pathId = query.get("path")?.toLongOrNull()

    return when (pathPieces.size) {
        1 -> Destinations.Area(pathPieces[0])
        2 -> Destinations.Zone(pathPieces[0], pathPieces[1])
        3 -> Destinations.Sector(pathPieces[0], pathPieces[1], pathPieces[2], pathId)
        else -> null
    }
}
