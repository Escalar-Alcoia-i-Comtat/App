package org.escalaralcoiaicomtat.app.platform

import io.github.aakira.napier.Napier
import io.ktor.http.Url
import io.ktor.http.toURI
import java.awt.Desktop
import java.io.IOException
import java.net.URISyntaxException

/**
 * Opens the given [url] in the default browser.
 *
 * @param url The address to launch.
 *
 * @return `true` if the point was launched successfully. `false` if there was an error, or the url
 * could not be launched for any reason.
 */
actual fun launchUrl(url: Url): Boolean {
    return if (Desktop.isDesktopSupported()) {
        val desktop = Desktop.getDesktop()
        try {
            desktop.browse(url.toURI())
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
            runtime.exec(arrayOf("xdg-open", url.toString()))
            true
        } catch (e: IOException) {
            Napier.e(throwable = e) { "Could not launch point url." }
            false
        }
    }
}
