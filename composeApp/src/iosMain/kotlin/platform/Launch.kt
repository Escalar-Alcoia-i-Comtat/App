package platform

import data.generic.LatLng
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

private const val DEFAULT_ZOOM = 6

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
    // see: https://developers.google.com/maps/documentation/urls/ios-urlscheme

    // Check whether Google Maps is installed
    val url = if (
        UIApplication.sharedApplication.canOpenURL(NSURL.URLWithString("comgooglemaps://")!!)
    ){
        // Google Maps is available, launch in the app
        "comgooglemaps://?center=${point.longitude},${point.latitude}&zoom=$DEFAULT_ZOOM"
    } else {
        // Google Maps is not installed, launch in the browser
        "https://www.google.com/maps/@${point.longitude},${point.latitude},${DEFAULT_ZOOM}z"
    }

    return UIApplication.sharedApplication.openURL(
        NSURL.URLWithString(url)!!
    )
}
