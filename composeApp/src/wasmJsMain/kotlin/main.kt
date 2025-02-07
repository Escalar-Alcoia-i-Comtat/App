import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import cache.StorageProvider
import cache.storageProvider
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.url.URLSearchParams
import ui.Locales
import ui.navigation.Destination
import ui.navigation.Destinations

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()

    // Configure Push Notifications
    NotifierManager.initialize(
        NotificationPlatformConfiguration.Web(
            askNotificationPermissionOnStart = false,
        )
    )

    calculateStartDestination()?.let { startDestination ->
        // If startDestination is not null, it means that the current path is not root. Right now, even
        // though SAJ is configured, resources are always loaded using relative URLs, so no resources
        // can be resolved from /area/1, for example.
        // Because of this, when loading the website from a relative URL, we need to redirect the user
        // to root, and then introduce the desired location into the history.
        storeStartDestinationToSession(startDestination)
        window.location.replace("/")
        return
    }

    val startDestination = getStartDestinationFromSession()

    ComposeViewport(document.body!!) {
        LaunchedEffect(Unit) {
            val lang = window.navigator.language
            document.body?.lang = Locales.valueOf(lang)
        }

        AppRoot(startDestination = startDestination)
    }
}

private fun storeStartDestinationToSession(destination: Destination) {
    val string = Json.encodeToString(Destination.serializer(), destination)
    sessionStorage.setItem("destination", string)
}

private fun getStartDestinationFromSession(): Destination? {
    val destinationString = sessionStorage["destination"] ?: return null
    try {
        return Json.decodeFromString(Destination.serializer(), destinationString)
    } catch (_: SerializationException) {
        Napier.e { "Got invalid destination from session: $destinationString" }
    } catch (_: IllegalArgumentException) {
        Napier.e { "Got invalid destination from session: $destinationString" }
    } finally {
        sessionStorage.removeItem("destination")
    }
    return null
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
