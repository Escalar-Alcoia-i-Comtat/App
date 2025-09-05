package org.escalaralcoiaicomtat.app.ui.platform

import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import build.BuildKonfig
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.StaticMapCriteria
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation
import com.mapbox.api.staticmap.v1.models.StaticPolylineAnnotation
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.map.kmz.KMZLoader
import org.escalaralcoiaicomtat.app.map.placemark.Polygon
import org.escalaralcoiaicomtat.app.network.createHttpClient
import org.escalaralcoiaicomtat.app.utils.asJavaFile
import kotlin.uuid.Uuid

actual class MapViewModel actual constructor() : ViewModel() {
    private val httpClient = createHttpClient {
        install(HttpCache) {
            val cacheFile = storageProvider.cacheDirectory + "map_cache"
            publicStorage(FileStorage(cacheFile.asJavaFile))
        }
    }

    private val _progress = MutableStateFlow<Float?>(null)
    val progress: StateFlow<Float?>
        get() = _progress.asStateFlow()

    private val _mapImage = MutableStateFlow<ByteArray?>(null)
    val mapImage: StateFlow<ByteArray?>
        get() = _mapImage.asStateFlow()

    fun loadMap(kmz: Uuid, layoutSize: IntSize) {
        viewModelScope.launch(Dispatchers.IO) {
            Napier.i { "Loading KMZ $kmz..." }
            val mapData = KMZLoader.loadKMZ(kmz)

            val (mapUUID, staticMap) = createMap(mapData, layoutSize)
            loadMap(mapUUID, staticMap)
        }
    }

    private fun createMap(mapData: MapData, layoutSize: IntSize): Pair<String, MapboxStaticMap> {
        val uuidBuilder = StringBuilder("")

        mapData.styles.let { Napier.d { "There are ${it.size} styles loaded." } }

        val staticMap = MapboxStaticMap.builder()
            .accessToken(BuildKonfig.MAPBOX_ACCESS_TOKEN!!)
            .styleId(StaticMapCriteria.OUTDOORS_STYLE)
            .also { uuidBuilder.append(StaticMapCriteria.OUTDOORS_STYLE) }
            .cameraAuto(true)
            .also { uuidBuilder.append(";auto") }
            .width(layoutSize.width)
            .also { uuidBuilder.append(";${layoutSize.width}") }
            .height(layoutSize.height)
            .also { uuidBuilder.append(";${layoutSize.height}") }
            .staticPolylineAnnotations(
                mapData.placemarks
                    .filterIsInstance<Polygon>()
                    // Log markers count
                    .also { Napier.d { "There are ${it.size} polygons in map." } }
                    .map { polygon ->
                        val pointsHashCode = polygon.coordinates
                            .joinToString(";") { (lat, lon) -> "$lat,$lon" }
                            .hashCode()
                        uuidBuilder.append(";${POLYLINE_PRECISION}")
                        uuidBuilder.append(";$pointsHashCode")

                        StaticPolylineAnnotation.builder()
                            .polyline(
                                PolylineUtils.encode(
                                    polygon.coordinates.map { (lat, lon) ->
                                        Point.fromLngLat(
                                            lat,
                                            lon
                                        )
                                    },
                                    POLYLINE_PRECISION
                                )
                            )
                            .build()
                    }
            )
            .staticMarkerAnnotations(
                mapData.placemarks
                    // Take only points
                    .filterIsInstance<org.escalaralcoiaicomtat.app.map.placemark.Point>()
                    // Sort from top to bottom to display ordered
                    .sortedByDescending { it.longitude }
                    // Log markers count
                    .also { Napier.d { "There are ${it.size} markers in map." } }
                    .map { point ->
                        uuidBuilder.append(";${point.latitude},${point.longitude}")

                        // TODO: icon from image
                        StaticMarkerAnnotation.builder()
                            .lnglat(Point.fromLngLat(point.latitude, point.longitude))
                            .name(StaticMapCriteria.SMALL_PIN)
                            .build()
                    }
            )
            .build()
        val mapUUID = uuidBuilder.toString()

        return mapUUID to staticMap
    }

    private suspend fun loadMap(mapUUID: String, map: MapboxStaticMap) = try {
        _progress.emit(null)
        val imageUrl = map.url().toString()
            // Replace problematic characters by URL encoding
            .replace("[", "%5B")
            .replace("]", "%5D")
        val imageUid = mapUUID.hashCode().toString()

        Napier.v { "Fetching map image ($imageUid): $imageUrl" }
        val result = httpClient.get(imageUrl) {
            onDownload { bytesSentTotal, contentLength ->
                contentLength ?: return@onDownload
                Napier.v { "Map image download progress: $bytesSentTotal / $contentLength" }
                _progress.emit((bytesSentTotal.toDouble() / contentLength.toDouble()).toFloat())
            }
        }
        if (result.status.value in 200..299) {
            Napier.v { "Map downloaded successfully. Reading body..." }
            val bytes = result.bodyAsBytes()
            Napier.v { "Emitting response bytes to UI..." }
            _mapImage.emit(bytes)
        } else {
            Napier.e { "Could not load map image. Status: ${result.status}" }
            _mapImage.emit(ByteArray(0))
        }
    } finally {
        _progress.emit(null)
    }

    companion object {
        private const val POLYLINE_PRECISION = 5
    }
}
