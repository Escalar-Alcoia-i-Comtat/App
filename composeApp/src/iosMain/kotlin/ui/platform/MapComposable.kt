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
import map.Style
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

data class MapData(
    val placemarks: List<Placemark>,
    val styles: List<Style>
)

private suspend inline fun loadKMZ(
    kmzUUID: String,
    onDocumentLoaded: (data: MapData) -> Unit
) {
    val kmlFile = KMZHandler.load(kmzUUID, replaceImagePaths = false)
    val kmlString = kmlFile.readAllBytes().decodeToString()
    val dataDir = kmlFile.parent
    val xml = Ksoup.parse(kmlString)
    val document = xml.root().getElementsByTag("Document")[0]
    val styles = document.getElementsByTag("Style").mapNotNull(Style::parse)
    val placemarks = document.getElementsByTag("Folder")
        .flatMap { folder ->
            folder.getElementsByTag("Placemark").map { Placemark.parse(it) }
        }
        .filterNotNull()
    onDocumentLoaded(
        MapData(placemarks, styles)
    )
}

fun transform(c: Pair<Double, Double>): Pair<Double, Double> {
    return if (c.second < 0) {
        c.first to (360 + c.second)
    } else {
        c
    }
}

fun inverseTransform(c: Pair<Double, Double>): Pair<Double, Double> {
    return if (c.second > 180) {
        c.first to (-360 + c.second)
    } else {
        c
    }
}

@ExperimentalForeignApi
fun regionForPoints(coordinates: List<Pair<Double, Double>>): CValue<MKCoordinateRegion>? {
    return if (coordinates.isEmpty()) {
        null
    } else if (coordinates.size == 1) {
        val center = coordinates[0]

        MKCoordinateRegionMake(
            CLLocationCoordinate2DMake(center.first, center.second),
            MKCoordinateSpanMake(1.0, 1.0)
        )
    } else {
        val transformed = coordinates.map(::transform)
        val minLat = transformed.minOf { it.first }
        val maxLat = transformed.maxOf { it.first }
        val minLon = transformed.minOf { it.second }
        val maxLon = transformed.maxOf { it.second }

        val latitudeDelta = maxLat - minLat
        val longitudeDelta = maxLon - minLon
        val span = MKCoordinateSpanMake(
            latitudeDelta,
            longitudeDelta
        )
        val center = inverseTransform(
            (maxLat - latitudeDelta / 2) to (maxLon - longitudeDelta / 2)
        )

        val (lat, lon) = center
        Napier.i { "Center is on ${lat},${lon}" }
        MKCoordinateRegionMake(CLLocationCoordinate2DMake(lat, lon), span)
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapComposable(modifier: Modifier, kmzUUID: String?) {
    var mapData by remember { mutableStateOf<MapData?>(null) }

    LaunchedEffect(kmzUUID) {
        if (kmzUUID != null) CoroutineScope(Dispatchers.IO).launch {
            loadKMZ(kmzUUID) { mapData = it }
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

            mapData?.let { data ->
                val points = mutableListOf<Pair<Double, Double>>()

                Napier.d { "Loaded ${data.styles.size} styles." }
                Napier.d { "Drawing ${data.placemarks.size} placemarks..." }
                for (placemark in data.placemarks) {
                    placemark.addToPoints(points)
                    placemark.addToMap(mapView, data.styles)
                }

                regionForPoints(points)?.let {
                    mapView.setRegion(it)
                }
            }
        }
    )
}
