package org.escalaralcoiaicomtat.app.ui.platform

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.map.placemark.Placemark
import org.escalaralcoiaicomtat.app.map.style.Style
import org.escalaralcoiaicomtat.app.map.utils.coordinateRegionOf
import org.escalaralcoiaicomtat.app.ui.reusable.CircularProgressIndicatorBox
import org.jetbrains.compose.resources.stringResource
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
    val error by viewModel.error.collectAsState()

    val clipboard = LocalClipboard.current

    LaunchedEffect(kmz) {
        if (kmz != null && mapData == null) viewModel.loadKMZ(kmz) { mapData = it }
    }

    OutlinedCard(modifier) {
        AnimatedContent(mapData) { data ->
            if (error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background( MaterialTheme.colorScheme.errorContainer)
                        .clickable {
                            viewModel.copyException(clipboard)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Map,
                            stringResource(Res.string.error_map_title),
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Text(
                            text = stringResource(Res.string.error_map_title),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(Res.string.error_map_title),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else if (data != null) {
                val mapView = remember {
                    MKMapView().apply {
                        mapType = MKMapTypeSatellite
                        // Disable all gestures
                        zoomEnabled = false
                        scrollEnabled = false
                        pitchEnabled = false
                        rotateEnabled = false
                    }
                }
                UIKitView(
                    factory = { mapView },
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics {
                            role = Role.Image
                        },
                    update = {
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
                        if (points.isNotEmpty()) {
                            coordinateRegionOf(points).let {
                                mapView.setRegion(it)
                            }
                        }
                    },
                    properties = UIKitInteropProperties(
                        isInteractive = false,
                        isNativeAccessibilityEnabled = false,
                    )
                )
            } else {
                CircularProgressIndicatorBox()
            }
        }
    }
}
