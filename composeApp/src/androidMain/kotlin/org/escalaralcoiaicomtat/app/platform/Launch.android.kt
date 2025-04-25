package org.escalaralcoiaicomtat.app.platform

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import io.ktor.http.Url
import org.escalaralcoiaicomtat.android.applicationContext

/**
 * Opens the given [url] in the default browser.
 *
 * @param url The address to launch.
 *
 * @return `true` if the point was launched successfully. `false` if there was an error, or the url
 * could not be launched for any reason.
 */
actual fun launchUrl(url: Url): Boolean {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url.toString())
        addFlags(FLAG_ACTIVITY_NEW_TASK)
    }
    return if (intent.resolveActivity(applicationContext.packageManager) != null) {
        applicationContext.startActivity(intent)
        true
    } else {
        false
    }
}
