package org.escalaralcoiaicomtat.app.platform

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import org.escalaralcoiaicomtat.android.applicationContext
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
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(
            if (label == null) {
                "geo:${point.latitude},${point.longitude}"
            } else {
                "geo:0,0?q=${point.latitude},${point.longitude}($label)"
            }
        )
        addFlags(FLAG_ACTIVITY_NEW_TASK)
    }
    return if (intent.resolveActivity(applicationContext.packageManager) != null) {
        applicationContext.startActivity(intent)
        true
    } else {
        false
    }
}
