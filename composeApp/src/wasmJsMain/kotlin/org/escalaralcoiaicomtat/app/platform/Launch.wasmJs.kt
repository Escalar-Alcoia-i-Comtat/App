package org.escalaralcoiaicomtat.app.platform

import io.ktor.http.Url
import kotlinx.browser.window
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.w3c.dom.Window

/**
 * Shows the given [point] in an external application.
 *
 * [Reference](https://developers.google.com/maps/documentation/urls/get-started#search-action)
 *
 * @param point The point to be launched.
 * @param label The label to display on the point.
 *
 * @return `true` if the point was launched successfully. `false` if there was an error, or the point could not be
 * launched for any reason.
 */
actual fun launchPoint(point: LatLng, label: String?): Boolean {
    val url = "https://www.openstreetmap.org/?mlat=${point.latitude}&mlon=${point.longitude}#map=17/${point.latitude}/${point.longitude}"

    return window.open(url, "_blank")?.also(Window::focus) != null
}

/**
 * Opens the given [url] in the default browser.
 *
 * @param url The address to launch.
 *
 * @return `true` if the point was launched successfully. `false` if there was an error, or the url
 * could not be launched for any reason.
 */
actual fun launchUrl(url: Url): Boolean {
    return window.open(url.toString(), "_blank")?.also(Window::focus) != null
}
