package platform

import data.generic.LatLng
import kotlinx.browser.window
import org.w3c.dom.Window

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

    return window.open(url, "_blank")?.also(Window::focus) != null
}
