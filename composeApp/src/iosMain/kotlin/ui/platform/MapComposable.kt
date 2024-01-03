package ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.fleeksoft.ksoup.Ksoup
import io.github.aakira.napier.Napier
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.pointed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import map.Placemark
import maps.KMZHandler
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapView
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private suspend inline fun loadKMZ(kmzUUID: String, onDocumentLoaded: (List<Placemark>) -> Unit) {
    val kmlFile = KMZHandler.load(kmzUUID, replaceImagePaths = false)
    val kmlString = kmlFile.readAllBytes().decodeToString()
    val dataDir = kmlFile.parent
    val xml = Ksoup.parse(kmlString)
    val document = xml.root().getElementsByTag("Document")[0]
    val placemarks = document.getElementsByTag("Folder")
        .flatMap { folder ->
            folder.getElementsByTag("Placemark").map { Placemark.parse(it) }
        }
        .filterNotNull()
    onDocumentLoaded(placemarks)
}

fun degreesToRadians(degrees: Double): Double = PI * degrees / 180.0

fun radiansToDegrees(radians: Double): Double = radians * 180.0 / PI

fun midPoint(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Pair<Double, Double> {
    val dLon = degreesToRadians(lon2 - lon1)

    val lat1r = degreesToRadians(lat1)
    val lat2r = degreesToRadians(lat2)
    val lon1r = degreesToRadians(lon1)

    val bx = cos(lat2) * cos(dLon)
    val by = cos(lat2) * sin(dLon)

    val lat3 = atan2(sin(lat1r) + sin(lat2r), sqrt((cos(lat1r) + bx) * (cos(lat1r) + bx) + by * by))
    val lon3 = lon1r + atan2(by, cos(lat1r) + bx)
    return radiansToDegrees(lat3) to radiansToDegrees(lon3)
}

@OptIn(ExperimentalForeignApi::class)
private fun computeCentroid(points: List<CLLocationCoordinate2D>): CLLocationCoordinate2D? {
    if (points.isEmpty()) return null

    var latitude = points[0].latitude
    var longitude = points[1].longitude

    for (point in points) {
        val (lat, lon) = midPoint(latitude, longitude, point.latitude, point.longitude)
        latitude = lat
        longitude = lon
    }

    val center = CLLocationCoordinate2DMake(latitude, longitude)
    return interpretCPointer<CLLocationCoordinate2D>(center.objcPtr())!!.pointed
}

@OptIn(ExperimentalForeignApi::class)
fun transform(c: CLLocationCoordinate2D): CLLocationCoordinate2D {
    return if (c.longitude < 0) {
        val p = CLLocationCoordinate2DMake(c.latitude, 360 + c.longitude)
        interpretCPointer<CLLocationCoordinate2D>(p.objcPtr())!!.pointed
    } else {
        c
    }
}

@OptIn(ExperimentalForeignApi::class)
fun inverseTransform(c: CLLocationCoordinate2D): CLLocationCoordinate2D {
    return if (c.longitude > 180) {
        val p = CLLocationCoordinate2DMake(c.latitude, -360 + c.longitude)
        interpretCPointer<CLLocationCoordinate2D>(p.objcPtr())!!.pointed
    } else {
        c
    }
}

@ExperimentalForeignApi
fun regionForPoints(coordinates: List<CLLocationCoordinate2D>): CValue<MKCoordinateRegion>? {
    return if (coordinates.isEmpty()) {
        null
    } else if (coordinates.size == 1) {
        val center = coordinates[0]

        MKCoordinateRegionMake(
            CLLocationCoordinate2DMake(center.latitude, center.longitude),
            MKCoordinateSpanMake(1.0, 1.0)
        )
    } else {
        /*val transformed = coordinates.map(::transform)
        val minLat = transformed.minOf { it.latitude }
        val maxLat = transformed.maxOf { it.latitude }
        val minLon = transformed.minOf { it.longitude }
        val maxLon = transformed.maxOf { it.longitude }

        val latitudeDelta = maxLat - minLat
        val longitudeDelta = maxLon - minLon
        val span = MKCoordinateSpanMake(latitudeDelta, longitudeDelta)
        val center = inverseTransform(
            interpretCPointer<CLLocationCoordinate2D>(
                CLLocationCoordinate2DMake(
                    maxLat - latitudeDelta / 2,
                    maxLon - longitudeDelta / 2
                ).objcPtr()
            )!!.pointed
        )*/

        val span = MKCoordinateSpanMake(1.0, 1.0)
        val center = computeCentroid(coordinates)!!

        // FIXME - Camera movement. It's not centered correctly
        Napier.i { "Center is on ${center.latitude},${center.longitude}" }
        MKCoordinateRegionMake(CLLocationCoordinate2DMake(center.latitude, center.longitude), span)
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapComposable(modifier: Modifier, kmzUUID: String?) {
    var placemarks by remember { mutableStateOf<List<Placemark>?>(null) }

    LaunchedEffect(kmzUUID) {
        if (kmzUUID != null) CoroutineScope(Dispatchers.IO).launch {
            loadKMZ(kmzUUID) { placemarks = it }
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            MKMapView().apply {
                // Disable all gestures
                zoomEnabled = false
                scrollEnabled = false
                pitchEnabled = false
                rotateEnabled = false
            }
        },
        update = { mapView ->
            // Clear all annotations
            Napier.d { "Removing ${mapView.annotations.size} annotations..." }
            for (annotation in mapView.annotations) {
                mapView.removeAnnotation(annotation as MKAnnotationProtocol)
            }

            placemarks?.let { list ->
                val points = mutableListOf<CLLocationCoordinate2D>()

                for (item in list) {
                    item.addToPoints(points)
                    item.addToMap(mapView)
                }

                regionForPoints(points)?.let {
                    mapView.setRegion(it)
                }
            }
        }
    )
}
