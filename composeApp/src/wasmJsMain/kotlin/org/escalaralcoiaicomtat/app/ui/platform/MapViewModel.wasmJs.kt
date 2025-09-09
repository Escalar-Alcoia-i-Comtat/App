package org.escalaralcoiaicomtat.app.ui.platform

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.map.kmz.KMZLoader
import org.escalaralcoiaicomtat.app.map.placemark.Placemark
import org.escalaralcoiaicomtat.app.map.placemark.Point
import org.escalaralcoiaicomtat.app.map.placemark.Polygon
import org.escalaralcoiaicomtat.app.map.placemark.pathData
import org.escalaralcoiaicomtat.app.map.style.IconStyle
import org.escalaralcoiaicomtat.app.map.style.LineStyle
import org.escalaralcoiaicomtat.app.map.style.PolyStyle
import org.escalaralcoiaicomtat.app.map.utils.RegionUtils
import org.escalaralcoiaicomtat.app.map.utils.latLonToNormalizedWebMercator
import org.escalaralcoiaicomtat.app.map.utils.locationToNormalizedWebMercator
import org.escalaralcoiaicomtat.app.network.BasicBackend
import org.escalaralcoiaicomtat.app.utils.IO
import ovh.plrapps.mapcompose.api.ExperimentalClusteringApi
import ovh.plrapps.mapcompose.api.addCallout
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.addLazyLoader
import ovh.plrapps.mapcompose.api.addMarker
import ovh.plrapps.mapcompose.api.addPath
import ovh.plrapps.mapcompose.api.centroidX
import ovh.plrapps.mapcompose.api.centroidY
import ovh.plrapps.mapcompose.api.enableFlingZoom
import ovh.plrapps.mapcompose.api.enableScrolling
import ovh.plrapps.mapcompose.api.enableZooming
import ovh.plrapps.mapcompose.api.onMarkerClick
import ovh.plrapps.mapcompose.api.onPathClick
import ovh.plrapps.mapcompose.api.onTap
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.api.scrollTo
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.layout.Forced
import ovh.plrapps.mapcompose.ui.state.MapState
import ovh.plrapps.mapcompose.ui.state.markers.model.RenderingStrategy
import kotlin.math.pow
import kotlin.uuid.Uuid

actual class MapViewModel actual constructor() : ViewModel() {
    companion object {
        private const val TILE_SIZE = 256

        private const val MAX_ZOOM_LEVEL = 16
        private const val MIN_ZOOM_LEVEL = 12

        private const val MARKER_LAZY_LOADER = "lazy-marker"
    }

    val tileStreamProvider = TileStreamProvider { row, col, zoomLvl ->
        BasicBackend.urlToRawSource(
            "https://maps.escalaralcoiaicomtat.org/tile/$zoomLvl/$col/$row.png"
        )
    }

    private var loadedKmz: Uuid? = null
    private val placemarkPoints: MutableMap<String, Point> = mutableMapOf()
    private val placemarkPolygons: MutableMap<String, Polygon> = mutableMapOf()

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

    fun zoom(centroidX: Double, centroidY: Double, scale: Float) {
        val state = state.value ?: return
        viewModelScope.launch {
            state.scrollTo(
                centroidX,
                centroidY,
                state.scale * scale,
                TweenSpec(500, easing = FastOutSlowInEasing)
            )
        }
    }

    actual fun zoomIn() {
        val state = state.value ?: return
        zoom(state.centroidX, state.centroidY, 1.5f)
    }

    actual fun zoomOut() {
        val state = state.value ?: return
        zoom(state.centroidX, state.centroidY, 1 / 1.5f)
    }

    private fun MapState.addCallout(id: String, x: Double, y: Double, placemark: Placemark) {
        addCallout(
            id = "$id-callout",
            x = x,
            y = y,
            relativeOffset = Offset(-0.5f, -2f)
        ) {
            OutlinedCard {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = placemark.name,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                placemark.description?.let { description ->
                    Text(
                        text = description,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    @OptIn(ExperimentalClusteringApi::class)
    fun loadMap(kmz: Uuid, blockInteractions: Boolean, onMapClick: (() -> Unit)?) {
        if (loadedKmz == kmz) return

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
            addLazyLoader(MARKER_LAZY_LOADER)

            if (!blockInteractions) {
                enableZooming()
                enableScrolling()
                enableFlingZoom()
            }
            if (onMapClick != null) {
                onTap { _, _ -> onMapClick() }
            }

            onMarkerClick { id, x, y ->
                placemarkPoints[id]?.let { point ->
                    addCallout(id, x, y, point)
                }
            }

            onPathClick { id, x, y ->
                placemarkPolygons[id]?.let { polygon ->
                    addCallout(id, x, y, polygon)
                }
            }

            scale = 0.0
        }
        _state.tryEmit(mapState)

        viewModelScope.launch(Dispatchers.IO) {
            Napier.i { "Loading KMZ $kmz..." }
            val mapData = KMZLoader.loadKMZ(kmz)
            val iconStyles = mapData.styles.filterIsInstance<IconStyle>()

            Napier.d { "Got ${mapData.styles.size} styles: ${mapData.styles.joinToString { it.id }}" }
            Napier.d {
                "Got ${mapData.iconStyleData.size} icon image files: ${
                    mapData.iconStyleData.toList().joinToString { it.first }
                }"
            }
            Napier.d { "Got ${mapData.placemarks.size} placemarks." }

            val points = mutableListOf<ovh.plrapps.mapcompose.utils.Point>()

            placemarkPoints.clear()
            for (placemark in mapData.placemarks) {
                val id = placemark.generateId()
                if (placemark is Point) {
                    placemarkPoints[id] = placemark

                    val iconStyle = iconStyles.find { placemark.styleUrl?.endsWith(it.id) == true }
                    val icon = iconStyle?.iconHref?.let { mapData.iconStyleData[it] }
                    if (placemark.styleUrl != null && icon == null) {
                        Napier.w { "No icon image found at: ${iconStyle?.iconHref}" }
                    }

                    val point = placemark.locationToNormalizedWebMercator()
                    points += point
                    Napier.d { "Adding marker (${placemark.name} @ ${placemark.latitude}, ${placemark.longitude} - icon href: ${iconStyle?.iconHref})..." }
                    mapState.addMarker(
                        id = id,
                        x = point.x,
                        y = point.y,
                        zIndex = 2f,
                        clickable = true,
                        renderingStrategy = RenderingStrategy.LazyLoading(MARKER_LAZY_LOADER)
                    ) {
                        if (icon != null) {
                            AsyncImage(
                                model = icon,
                                contentDescription = placemark.description,
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = placemark.description,
                            )
                        }
                    }
                } else if (placemark is Polygon) {
                    placemarkPolygons[id] = placemark

                    val styleMap = mapData.findStyleMapByUrl(placemark.styleUrl)
                    val normalStyleUrl = styleMap?.getNormalStyleUrl()
                    val placemarkStyleUrl = normalStyleUrl ?: placemark.styleUrl

                    val style = mapData.findStyleByUrl(placemarkStyleUrl)
                    val pathData = placemark.pathData(mapState) ?: continue
                    Napier.d { "Adding path (${placemark.name} style ($placemarkStyleUrl)=$style) with ${pathData.size} points..." }
                    mapState.addPath(
                        id = id,
                        pathData = pathData,
                        zIndex = 2f,
                        clickable = true,
                        width = (style as? LineStyle)?.lineWidth?.dp,
                        color = (style as? LineStyle)?.lineColor(),
                        fillColor = (style as? PolyStyle)?.fillColor(),
                    )
                }
            }

            val (centroidX, centroidY) = RegionUtils.computeCentroid(points)
            mapState.scrollTo(centroidX, centroidY, 1.0)

            // val points = mutableListOf<LatLng>()
            // mapData.placemarks.forEach { it.addToPoints(points) }

            /*val boundingBox = RegionUtils.boundingBoxForPoints(points)
            if (boundingBox == null) {
                Napier.e { "No bounding box found for points $points" }
                return@launch
            }
            Napier.d { "Moving camera to: $boundingBox" }
            mapState.scrollTo(boundingBox)*/

            /*points.firstOrNull()?.let {
                val (x, y) = latLonToNormalizedWebMercator(it.latitude, it.longitude)
                mapState.scrollTo(x, y, 10.0)
            }*/

            loadedKmz = kmz
        }
    }
}
