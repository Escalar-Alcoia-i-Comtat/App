package org.escalaralcoiaicomtat.app.platform

import io.ktor.http.Url
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * Opens the given [url] in the default browser.
 *
 * @param url The address to launch.
 *
 * @return `true` if the point was launched successfully. `false` if there was an error, or the url
 * could not be launched for any reason.
 */
actual fun launchUrl(url: Url): Boolean {
    return UIApplication.sharedApplication.openURL(
        NSURL.URLWithString(url.toString())!!
    )
}
