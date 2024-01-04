package map.placemark

import com.fleeksoft.ksoup.nodes.Element
import io.github.aakira.napier.Napier
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.objcPtr
import map.parser.PlacemarkParser
import map.style.Style
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKMapView
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolygonRenderer
import platform.MapKit.addOverlay
import platform.MapKit.rendererForOverlay
import platform.UIKit.UIColor

data class Polygon(
    override val name: String,
    override val description: String?,
    override val styleUrl: String?,
    val coordinates: List<Pair<Double, Double>>
): Placemark {
    companion object: PlacemarkParser<Polygon> {
        override fun parse(style: Element): Polygon? {
            return style.getElementsByTag("Polygon").firstOrNull()?.let { element ->
                val coordinates = element.getElementsByTag("outerBoundaryIs")[0]
                    .getElementsByTag("LinearRing")[0]
                    .getElementsByTag("coordinates")[0]
                    .text()
                    .split(" ")
                    .map { it.trim().split(',') }
                    .map { it[0].toDouble() to it[1].toDouble() }

                Polygon(
                    style.getElementsByTag("name").first()!!.text(),
                    style.getElementsByTag("description").firstOrNull()?.text(),
                    style.getElementsByTag("styleUrl").firstOrNull()?.text(),
                    coordinates
                )
            }
        }
    }

    override fun addToPoints(list: MutableList<Pair<Double, Double>>) {
        list.addAll(coordinates)
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun addToMap(mapView: MKMapView, styles: List<Style>) {
        val coordinates = coordinates.map { (lat, lon) ->
            CLLocationCoordinate2DMake(lat, lon)
        }
        val polygon = MKPolygon.polygonWithCoordinates(
            interpretCPointer(coordinates.objcPtr()),
            coordinates.size.toULong()
        )
        Napier.v(tag = "Polygon") { "Adding polygon of ${coordinates.size} points to map." }
        mapView.addOverlay(polygon)
        // TODO: Renderer doesn't work. Looks like we have to extend something with Swift
        mapView.rendererForOverlay(polygon)?.let { renderer ->
            renderer as MKPolygonRenderer
            renderer.strokeColor = UIColor.blueColor
            renderer.lineWidth = 4.0
            renderer.fillColor = UIColor.redColor
        }
    }
}
