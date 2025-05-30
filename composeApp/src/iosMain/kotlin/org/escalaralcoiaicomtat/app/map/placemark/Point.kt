package org.escalaralcoiaicomtat.app.map.placemark

import com.fleeksoft.ksoup.nodes.Element
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.map.parser.PlacemarkParser
import org.escalaralcoiaicomtat.app.map.style.IconStyle
import org.escalaralcoiaicomtat.app.map.style.Style
import org.escalaralcoiaicomtat.app.map.utils.findById
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

    val latitude: Double = coordinates.split(',')[1].toDouble()
    val longitude: Double = coordinates.split(',')[0].toDouble()

    val latLng: LatLng = LatLng(latitude, longitude)
    val coordinate = CLLocationCoordinate2DMake(latitude, longitude)

    override fun addToPoints(list: MutableList<LatLng>) {
        list.add(latLng)
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
