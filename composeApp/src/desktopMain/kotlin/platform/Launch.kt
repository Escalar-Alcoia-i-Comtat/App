package platform

import data.generic.LatLng
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments

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
        .appendPathSegments("maps", "@${point.latitude},${point.longitude},15z")
        .build()

    return launchUrl(url)
}
