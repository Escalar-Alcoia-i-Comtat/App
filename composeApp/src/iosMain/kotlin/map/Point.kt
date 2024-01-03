package map

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.pointed
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation

@OptIn(ExperimentalForeignApi::class)
data class Point(
    override val name: String,
    override val description: String?,
    override val styleUrl: String?,
    private val coordinates: String
): Placemark {
    val latitude: Double = coordinates.split(',')[0].toDouble()
    val longitude: Double = coordinates.split(',')[1].toDouble()

    val coordinate = CLLocationCoordinate2DMake(latitude, longitude)

    override fun addToPoints(list: MutableList<CLLocationCoordinate2D>) {
        val location = interpretCPointer<CLLocationCoordinate2D>(coordinate.objcPtr())!!
        list.add(
            location.pointed
        )
    }

    @ExperimentalForeignApi
    override fun addToMap(mapView: MKMapView) {
        val annotation = MKPointAnnotation(
            coordinate = coordinate,
            title = name,
            subtitle = description
        )
        mapView.addAnnotation(annotation)
    }
}
