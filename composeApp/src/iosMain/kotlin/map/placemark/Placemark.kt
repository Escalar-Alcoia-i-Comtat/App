package map.placemark

import com.fleeksoft.ksoup.nodes.Element
import map.style.Style
import platform.MapKit.MKMapView

interface Placemark {
    companion object {
        fun parse(placemark: Element): Placemark? {
            val point = Point.parse(placemark)
            val polygon = Polygon.parse(placemark)
            return when {
                point != null -> point
                polygon != null -> polygon
                else -> null
            }
        }
    }

    val name: String
    val description: String?
    val styleUrl: String?

    fun addToPoints(list: MutableList<Pair<Double, Double>>)

    fun addToMap(mapView: MKMapView, styles: List<Style>)
}
