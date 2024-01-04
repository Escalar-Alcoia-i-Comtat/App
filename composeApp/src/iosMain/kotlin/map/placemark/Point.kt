package map.placemark

import com.fleeksoft.ksoup.nodes.Element
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import map.parser.PlacemarkParser
import map.style.IconStyle
import map.style.Style
import map.utils.findById
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class)
data class Point(
    override val name: String,
    override val description: String?,
    override val styleUrl: String?,
    private val coordinates: String
): Placemark {
    companion object: PlacemarkParser<Point> {
        override fun parse(style: Element): Point? {
            return style.getElementsByTag("Point").firstOrNull()?.let { element ->
                Point(
                    style.getElementsByTag("name").first()!!.text(),
                    style.getElementsByTag("description").firstOrNull()?.text(),
                    style.getElementsByTag("styleUrl").firstOrNull()?.text(),
                    element.getElementsByTag("coordinates").first()!!.text()
                )
            }
        }
    }

    val latitude: Double = coordinates.split(',')[0].toDouble()
    val longitude: Double = coordinates.split(',')[1].toDouble()

    val coordinate = CLLocationCoordinate2DMake(latitude, longitude)

    override fun addToPoints(list: MutableList<Pair<Double, Double>>) {
        list.add(
            latitude to longitude
        )
    }

    @ExperimentalForeignApi
    override fun addToMap(mapView: MKMapView, styles: List<Style>) {
        Napier.v(tag = "Point") { "Adding marker at $latitude,$longitude ($name) to map." }
        val annotation = MKPointAnnotation(
            coordinate = coordinate,
            title = name,
            subtitle = description
        )
        styleUrl?.let(styles::findById)?.takeIf { it is IconStyle }?.let { style ->
            style as IconStyle
            Napier.v(tag = "Point") { "  Setting image for $name: ${style.iconHref}" }
            val annotationView = MKAnnotationView(annotation, name).apply {
                image = UIImage.imageNamed("")
                // frame = CGRectMake(x = 0.0, y = 0.0, width = 25.0, height = 25.0)
            }
        }
        mapView.viewForAnnotation(annotation)
        mapView.addAnnotation(annotation)
    }
}
