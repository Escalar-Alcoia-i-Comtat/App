package map

import com.fleeksoft.ksoup.nodes.Element
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKMapView

interface Placemark {
    companion object {
        fun parse(placemark: Element): Placemark? {
            val point = placemark.getElementsByTag("Point").firstOrNull()
            return if (point != null) {
                Point(
                    placemark.getElementsByTag("name").first()!!.text(),
                    placemark.getElementsByTag("name").firstOrNull()?.text(),
                    placemark.getElementsByTag("name").firstOrNull()?.text(),
                    point.getElementsByTag("coordinates").first()!!.text()
                )
            } else {
                null
            }
        }
    }

    val name: String
    val description: String?
    val styleUrl: String?

    fun addToPoints(list: MutableList<CLLocationCoordinate2D>)

    fun addToMap(mapView: MKMapView)
}