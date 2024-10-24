package ui.platform

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.data.Feature
import com.google.maps.android.data.Geometry
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlLayer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import maps.KMZHandler

private fun scanContainer(container: KmlContainer): List<LatLng> {
    val points = mutableListOf<LatLng>()
    if (container.hasPlacemarks()) {
        Napier.v { "Analyzing ${container.placemarks.count()} placemarks..." }
        for (pm in container.placemarks) {
            pm.markerOptions?.also { Napier.d { "  Got 1 point" } }?.position?.let(points::add)
            pm.polygonOptions?.points?.also { Napier.d { "  Got ${it.size} point(s)" } }
                ?.let(points::addAll)
            pm.polylineOptions?.points?.also { Napier.d { "  Got ${it.size} point(s)" } }
                ?.let(points::addAll)
            points.addAll(
                analyzeGeometry(pm.geometry).also { Napier.d { "  Got ${it.size} point(s)" } }
            )
        }
    }
    if (container.hasContainers()) {
        Napier.v { "Analyzing ${container.containers.count()} containers..." }
        for (cn in container.containers) {
            points.addAll(
                scanContainer(cn)
            )
        }
    }
    return points
}

private fun analyzeObject(obj: Any?): MutableList<LatLng> {
    val points = mutableListOf<LatLng>()
    when (obj) {
        is Polyline -> {
            points.addAll(obj.points)
        }

        is Polygon -> {
            points.addAll(obj.points)
        }

        is Marker -> {
            points.add(obj.position)
        }

        is LatLng -> {
            points.add(obj)
        }

        is Iterable<*> -> {
            for (element in obj) {
                points.addAll(
                    analyzeObject(element)
                )
            }
        }

        else -> error("Got an unknown feature: ${obj?.let { it::class.simpleName }}")
    }
    return points
}

private fun <T> analyzeGeometry(geometry: Geometry<T>): List<LatLng> {
    return analyzeObject(geometry.geometryObject)
}

private fun analyzeFeatures(features: Iterable<Feature>): List<LatLng> {
    val points = mutableListOf<LatLng>()
    Napier.v { "Analyzing ${features.count()} features..." }
    for (ft in features) {
        points.addAll(
            analyzeGeometry(ft.geometry)
        )
    }
    return points
}

@WorkerThread
private suspend inline fun loadKMZ(
    context: Context,
    googleMap: GoogleMap,
    kmzUUID: String,
    onKmlLayerLoaded: (KmlLayer) -> Unit,
    crossinline onMoveCameraRequested: (CameraUpdate) -> Unit
) {
    val kml = KMZHandler.load(kmzUUID)
    kml.read("doc.xml")!!.inputStream().use { stream ->
        val layer = KmlLayer(googleMap, stream, context).also(onKmlLayerLoaded)
        withContext(Dispatchers.Main) {
            layer.addLayerToMap()
        }
        val points = mutableListOf<LatLng>()
        if (layer.hasPlacemarks()) {
            Napier.v { "Analyzing ${layer.placemarks.count()} placemarks..." }
            for (pm in layer.placemarks) {
                val position = pm.markerOptions?.position ?: continue
                points.add(position)
            }
        }
        if (layer.hasContainers()) {
            Napier.v { "Analyzing ${layer.containers.count()} containers..." }
            for (cn in layer.containers) {
                points.addAll(
                    scanContainer(cn)
                )
            }
        }
        analyzeFeatures(layer.features)

        if (points.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            for (point in points) boundsBuilder.include(point)
            withContext(Dispatchers.Main) {
                onMoveCameraRequested(
                    CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 2)
                )
            }
        }
    }
}

@Composable
@OptIn(MapsComposeExperimentalApi::class)
@SuppressLint("PotentialBehaviorOverride")
actual fun MapComposable(
    viewModel: MapViewModel,
    modifier: Modifier,
    kmzUUID: String?
) {
    val context = LocalContext.current

    var kmlLayer by remember { mutableStateOf<KmlLayer?>(null) }
    var loadComplete by remember { mutableStateOf(kmzUUID == null) }

    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        cameraPositionState = cameraPositionState,
        modifier = modifier,
        uiSettings = MapUiSettings(
            compassEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            rotationGesturesEnabled = false,
            scrollGesturesEnabled = false,
            scrollGesturesEnabledDuringRotateOrZoom = false,
            tiltGesturesEnabled = false,
            zoomGesturesEnabled = false,
            zoomControlsEnabled = false
        )
    ) {
        MapEffect(Unit) {
            // Skip default marker events
            it.setOnMarkerClickListener { true }
        }

        MapEffect(kmzUUID) { googleMap ->
            if (kmzUUID != null) {
                // Remove previous KML
                kmlLayer?.removeLayerFromMap()?.also { kmlLayer = null }

                CoroutineScope(Dispatchers.IO).launch {
                    loadKMZ(
                        context,
                        googleMap,
                        kmzUUID,
                        { kmlLayer = it },
                        cameraPositionState::move
                    )
                    withContext(Dispatchers.Main) { loadComplete = true }
                }
            }
        }
    }
}
