package org.escalaralcoiaicomtat.app.ui.platform

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.data.Feature
import com.google.maps.android.data.Geometry
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlLayer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.escalaralcoiaicomtat.app.maps.KMZHandler
import kotlin.uuid.Uuid

actual class MapViewModel actual constructor() : ViewModel() {

    private var _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    private var kmlLayer: KmlLayer? = null

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
    private suspend fun loadKMZ(
        context: Context,
        googleMap: GoogleMap,
        kmzUUID: Uuid,
        onMoveCameraRequested: (CameraUpdate) -> Unit
    ) {
        val kml = KMZHandler.load(kmzUUID)
        val data = kml.read("doc.kml") ?: error("Failed to read KML from KMZ")
        data.inputStream().use { stream ->
            val layer = KmlLayer(googleMap, stream, context).also { kmlLayer = it }

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

    fun load(
        context: Context,
        googleMap: GoogleMap,
        kmz: Uuid,
        onMoveCameraRequested: (CameraUpdate) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.emit(true)

                loadKMZ(context, googleMap, kmz, onMoveCameraRequested)
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun disposeLayer() {
        kmlLayer?.removeLayerFromMap()
        kmlLayer = null
    }
}
