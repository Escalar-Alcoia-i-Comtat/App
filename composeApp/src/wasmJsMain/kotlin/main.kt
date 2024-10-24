import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import cache.StorageProvider
import cache.storageProvider
import data.EDataType
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()

    val initial = calculateInitial()

    ComposeViewport(document.body!!) {
        AppRoot(initial = initial)
    }
}

private fun calculateInitial(): EDataType? {
    val params = URLSearchParams(window.location.search.toJsString())
    val area = params.get("area")?.toLongOrNull()
    val zone = params.get("zone")?.toLongOrNull()
    val sector = params.get("sector")?.toLongOrNull()
    val path = params.get("path")?.toLongOrNull()
    Napier.i { "area=$area, zone=$zone, sector=$sector, path=$path" }
    return when {
        path != null && sector != null -> EDataType.Path(sector, path)
        sector != null -> EDataType.Sector(sector)
        zone != null -> EDataType.Zone(zone)
        area != null -> EDataType.Area(area)
        else -> null
    }
}
