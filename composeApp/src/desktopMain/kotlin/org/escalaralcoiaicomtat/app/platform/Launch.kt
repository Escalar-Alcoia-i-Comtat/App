package org.escalaralcoiaicomtat.app.platform

import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import org.escalaralcoiaicomtat.app.data.generic.LatLng

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
    val url = URLBuilder("https://www.google.com")
        .appendPathSegments("maps", "search", "")
        .apply {
            parameters["api"] = "1"
            parameters["query"] = "${point.latitude},${point.longitude}"
        }
        .build()

    return launchUrl(url)
}
