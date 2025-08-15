package org.escalaralcoiaicomtat.app.platform

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.net.toUri
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
    val geoIntent = Intent(Intent.ACTION_VIEW).apply {
        data = if (label == null) {
            "geo:${point.latitude},${point.longitude}"
        } else {
            "geo:0,0?q=${point.latitude},${point.longitude}($label)"
        }.toUri()
        addFlags(FLAG_ACTIVITY_NEW_TASK)
    }
    val osmIntent = Intent(Intent.ACTION_VIEW).apply {
        data = "https://www.openstreetmap.org/?mlat=${point.latitude}&mlon=${point.longitude}#map=17/${point.latitude}/${point.longitude}".toUri()
        addFlags(FLAG_ACTIVITY_NEW_TASK)
    }
    return if (geoIntent.resolveActivity(applicationContext.packageManager) != null) {
        applicationContext.startActivity(geoIntent)
        true
    } else if (osmIntent.resolveActivity(applicationContext.packageManager) != null) {
        applicationContext.startActivity(osmIntent)
        true
    } else {
        false
    }
}
