package platform

import data.generic.LatLng
import io.github.aakira.napier.Napier
import java.awt.Desktop
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

/**
 * Shows the given [point] in an external application.
 *
 * @param point The point to be launched.
 * @param label The label to display on the point.
 *
 * @return `true` if the point was launched successfully. `false` if there was an error, or the point could not be
 * launched for any reason.
 */
actual fun launchPoint(point: LatLng, label: String?): Boolean {
    val url = "https://www.google.com/maps/@${point.latitude},${point.longitude},15z"

    return if (Desktop.isDesktopSupported()) {
        val desktop = Desktop.getDesktop()
        try {
            desktop.browse(URI(url))
            true
        } catch (e: IOException) {
            Napier.e(throwable = e) { "Could not launch point url." }
            false
        } catch (e: URISyntaxException) {
            Napier.e(throwable = e) { "Could not launch point url." }
            false
        }
    } else {
        val runtime = Runtime.getRuntime()
        try {
            runtime.exec("xdg-open $url")
            true
        } catch (e: IOException) {
            Napier.e(throwable = e) { "Could not launch point url." }
            false
        }
    }
}
