package platform

import data.generic.LatLng
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationDegrees
import platform.Foundation.NSCoder
import platform.Foundation.NSURL
import platform.Foundation.NSValue
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKLaunchOptionsMapCenterKey
import platform.MapKit.MKMapItem
import platform.MapKit.MKPlacemark
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
@OptIn(ExperimentalForeignApi::class)
actual fun launchPoint(point: LatLng, label: String?): Boolean {
    // see: https://developers.google.com/maps/documentation/urls/ios-urlscheme

    // Check whether Google Maps is installed
    val url = if (
        UIApplication.sharedApplication.canOpenURL(NSURL.URLWithString("comgooglemaps://")!!)
    ){
        // Google Maps is available, launch in the app
        "comgooglemaps://?center=${point.longitude},${point.latitude}&zoom=$DEFAULT_ZOOM"
    } else {
        // Google Maps is not installed, launch in Apple Maps
        // Convert the coordinates into platform-specific
        val latitude: CLLocationDegrees = point.latitude
        val longitude: CLLocationDegrees = point.longitude
        val coordinates = CLLocationCoordinate2DMake(latitude, longitude)
        // Create the Map item, and set the name
        val mapItem = MKMapItem(
            MKPlacemark(coordinates)
        ).apply {
            name = label
        }
        // Open the link
        val open = mapItem.openInMapsWithLaunchOptions(
            launchOptions = null
        )
        if (open) {
            // Link launched successfully
            null
        } else {
            // If the link could not be open, launch in browser
            "https://www.google.com/maps/@${point.longitude},${point.latitude},${DEFAULT_ZOOM}z"
        }
    }

    return if (url != null) {
        UIApplication.sharedApplication.openURL(
            NSURL.URLWithString(url)!!
        )
    } else {
        // If null, the link has already been launched successfully
        true
    }
}
