package org.escalaralcoiaicomtat.app.ui.platform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.map.kmz.KMZLoader
import org.escalaralcoiaicomtat.app.map.point.latitude
import org.escalaralcoiaicomtat.app.map.point.longitude
import org.escalaralcoiaicomtat.app.map.utils.RegionUtils
import org.escalaralcoiaicomtat.app.map.utils.latLonToNormalizedWebMercator
import org.escalaralcoiaicomtat.app.map.utils.scrollToLatLng
import org.escalaralcoiaicomtat.app.network.BasicBackend
import org.escalaralcoiaicomtat.app.utils.IO
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.enableFlingZoom
import ovh.plrapps.mapcompose.api.enableScrolling
import ovh.plrapps.mapcompose.api.enableZooming
import ovh.plrapps.mapcompose.api.onTap
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.api.scroll
import ovh.plrapps.mapcompose.api.scrollTo
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.layout.Forced
import ovh.plrapps.mapcompose.ui.state.MapState
import kotlin.math.pow
import kotlin.uuid.Uuid

actual class MapViewModel actual constructor() : ViewModel() {
    companion object {
        private const val TILE_SIZE = 256

        private const val MAX_ZOOM_LEVEL = 16
        private const val MIN_ZOOM_LEVEL = 12
    }

    val tileStreamProvider = TileStreamProvider { row, col, zoomLvl ->
        BasicBackend.urlToRawSource(
            "https://maps.escalaralcoiaicomtat.org/tile/$zoomLvl/$col/$row.png"
        )
    }

    private val _state = MutableStateFlow<MapState?>(null)
    val state get() = _state.asStateFlow()

    /**
     * wmts level are 0 based.
     * At level 0, the map corresponds to just one tile.
     */
    private fun mapSizeAtLevel(wmtsLevel: Int, tileSize: Int = TILE_SIZE): Int {
        return tileSize * 2.0.pow(wmtsLevel).toInt()
    }

    actual val supportsZoomButtons: Boolean = true

    actual fun zoomIn() {
        val state = state.value ?: return
        viewModelScope.launch {
            state.scrollTo(state.scroll.x, state.scroll.y, state.scale + 1f)
        }
    }

    actual fun zoomOut() {
        val state = state.value ?: return
        viewModelScope.launch {
            state.scrollTo(state.scroll.x, state.scroll.y, state.scale - 1f)
        }
    }

    fun loadMap(kmz: Uuid, blockInteractions: Boolean, onMapClick: (() -> Unit)?) {
        val mapSize = mapSizeAtLevel(MAX_ZOOM_LEVEL)
        val mapState = MapState(
            levelCount = MAX_ZOOM_LEVEL + 1,
            fullWidth = mapSize,
            fullHeight = mapSize,
            tileSize = TILE_SIZE
        ) {
            minimumScaleMode(Forced(1 / 2.0.pow(MAX_ZOOM_LEVEL - MIN_ZOOM_LEVEL)))

            val (x, y) = latLonToNormalizedWebMercator(38.75954, -0.4776685)
            scroll(x, y)
        }.apply {
            addLayer(tileStreamProvider)

            if (!blockInteractions) {
                enableZooming()
                enableScrolling()
                enableFlingZoom()
            }
            if (onMapClick != null) {
                onTap { _, _ -> onMapClick() }
            }

            scale = 0.0
        }
        _state.tryEmit(mapState)

        viewModelScope.launch(Dispatchers.IO) {
            Napier.i { "Loading KMZ $kmz..." }
            val mapData = KMZLoader.loadKMZ(kmz)

            val points = mutableListOf<Pair<Double, Double>>()
            mapData.placemarks.forEach { it.addToPoints(points) }
            val region = RegionUtils.regionForPoints(points)
            if (region == null) {
                Napier.e { "No region found for points $points" }
                return@launch
            }
            val center = region.center
            Napier.d { "Moving camera to ${center.latitude}, ${center.longitude}" }
            mapState.scrollToLatLng(center.latitude, center.longitude, 0.0)
        }
    }
}
