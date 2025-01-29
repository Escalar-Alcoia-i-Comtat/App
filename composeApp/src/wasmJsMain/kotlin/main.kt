import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import cache.StorageProvider
import cache.storageProvider
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams
import ui.navigation.Destination
import ui.navigation.Destinations

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()

    val startDestination = calculateStartDestination()

    ComposeViewport(document.body!!) {
        AppRoot(startDestination = startDestination)
    }
}

private fun calculateStartDestination(): Destination? {
    val pathPieces = window.location.pathname
        .split('/')
        .filterNot { it.isBlank() }

    val query = URLSearchParams(window.location.search.toJsString())
    val pathId = query.get("path")?.toLongOrNull()

    if (pathPieces.size != 2) return null
    val type = pathPieces[0]
    val id = pathPieces[1].toLongOrNull() ?: return null
    return when (type) {
        "area" -> Destinations.Area(id)
        "zone" -> Destinations.Zone(id)
        "sector" -> if (pathId != null) Destinations.Sector(id, pathId) else Destinations.Sector(id)
        else -> null
    }
}
