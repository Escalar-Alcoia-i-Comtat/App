package org.escalaralcoiaicomtat.app.platform

import io.ktor.http.Url
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
expect fun launchPoint(point: LatLng, label: String?): Boolean

/**
 * Opens the given [url] in the default browser.
 *
 * @param url The address to launch.
 *
 * @return `true` if the point was launched successfully. `false` if there was an error, or the url
 * could not be launched for any reason.
 */
expect fun launchUrl(url: Url): Boolean
