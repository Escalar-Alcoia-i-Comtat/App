package org.escalaralcoiaicomtat.app.map.utils

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalForeignApi::class)
fun coordinateRegionOf(coordinates: List<LatLng>): CValue<MKCoordinateRegion> {
    var minLatitude = 90.0
    var maxLatitude = -90.0
    var minLongitude = 180.0
    var maxLongitude = -180.0

    for (coordinate in coordinates) {
        minLatitude = min(minLatitude, coordinate.latitude)
        maxLatitude = max(maxLatitude, coordinate.latitude)
        minLongitude = min(minLongitude, coordinate.longitude)
        maxLongitude = max(maxLongitude, coordinate.longitude)
    }

    val centerLatitude = (minLatitude + maxLatitude) / 2
    val centerLongitude = (minLongitude + maxLongitude) / 2
    val latitudeDelta = maxLatitude - minLatitude
    val longitudeDelta = maxLongitude - minLongitude

    return MKCoordinateRegionMake(
        CLLocationCoordinate2DMake(centerLatitude, centerLongitude),
        MKCoordinateSpanMake(latitudeDelta, longitudeDelta)
    )
}
