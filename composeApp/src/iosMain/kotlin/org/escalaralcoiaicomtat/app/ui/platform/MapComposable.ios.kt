package org.escalaralcoiaicomtat.app.ui.platform

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.map.placemark.Placemark
import org.escalaralcoiaicomtat.app.map.style.Style
import org.escalaralcoiaicomtat.app.map.utils.coordinateRegionOf
import org.escalaralcoiaicomtat.app.ui.reusable.CircularProgressIndicatorBox
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKMapTypeSatellite
import platform.MapKit.MKMapView
import kotlin.uuid.Uuid

data class MapData(
    val placemarks: List<Placemark>,
    val styles: List<Style>
)

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapComposable(
    viewModel: MapViewModel,
    modifier: Modifier,
    kmz: Uuid?,
    blockInteractions: Boolean,
    onMapClick: (() -> Unit)?,
) {
    var mapData by remember { mutableStateOf<MapData?>(null) }

    LaunchedEffect(kmz) {
        if (kmz != null && mapData == null) viewModel.loadKMZ(kmz) { mapData = it }
    }

    OutlinedCard(modifier) {
        AnimatedContent(mapData) { data ->
            if (data != null) {
                UIKitView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        MKMapView().apply {
                            mapType = MKMapTypeSatellite
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

                        val points = mutableListOf<LatLng>()

                        Napier.d { "Loaded ${data.styles.size} styles." }
                        Napier.d { "Drawing ${data.placemarks.size} placemarks..." }
                        for (placemark in data.placemarks) {
                            placemark.addToPoints(points)
                            placemark.addToMap(mapView, data.styles)
                        }

                        Napier.d { "Points: $points" }
                        coordinateRegionOf(points).let {
                            mapView.setRegion(it)
                        }
                    }
                )
            } else {
                CircularProgressIndicatorBox()
            }
        }
    }
}
