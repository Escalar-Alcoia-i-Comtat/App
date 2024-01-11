package ui.platform

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import build.BuildKonfig
import cache.File
import cache.storageProvider
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.StaticMapCriteria
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation
import com.mapbox.geojson.Point
import image.decodeImage
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.toByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import map.MapData
import map.kmz.KMZLoader
import network.createHttpClient

private val httpClient = createHttpClient()

@Composable
actual fun MapComposable(modifier: Modifier, kmzUUID: String?) {
    var layoutSize: IntSize? by remember { mutableStateOf(null) }
    var mapData: MapData? by remember { mutableStateOf(null) }
    var staticMap: MapboxStaticMap? by remember { mutableStateOf(null) }
    var mapUUID: String? by remember { mutableStateOf(null) }
    var mapImage: ByteArray? by remember { mutableStateOf(null) }
    var loadingProgress: Float? by remember { mutableStateOf(null) }

    // Instantiate the map when layout is positioned
    LaunchedEffect(layoutSize, kmzUUID) {
        if (layoutSize == null) return@LaunchedEffect
        if (kmzUUID == null) return@LaunchedEffect

        CoroutineScope(Dispatchers.IO).launch {
            Napier.i { "Loading KMZ $kmzUUID..." }
            KMZLoader.loadKMZ(kmzUUID) { mapData = it }
        }
    }

    // Create the map when the KMZ has been loaded
    LaunchedEffect(mapData) {
        if (layoutSize == null) return@LaunchedEffect
        if (mapData == null) return@LaunchedEffect
        val data = mapData!!
        val size = layoutSize!!

        val uuidBuilder = StringBuilder("")


        val builder = MapboxStaticMap.builder()
            .accessToken(BuildKonfig.MAPBOX_ACCESS_TOKEN)
            .styleId(StaticMapCriteria.OUTDOORS_STYLE)
            .also { uuidBuilder.append(StaticMapCriteria.OUTDOORS_STYLE) }
            .cameraAuto(true)
            .also { uuidBuilder.append(";auto") }
            //.cameraPoint(Point.fromLngLat(-0.455466, 38.7326039))
            //.also { uuidBuilder.append(";-0.455466,38.7326039") }
            //.cameraZoom(8.0)
            //.also { uuidBuilder.append(";8") }
            .width(size.width)
            .also { uuidBuilder.append(";${size.width}") }
            .height(size.height)
            .also { uuidBuilder.append(";${size.height}") }
            .staticMarkerAnnotations(
                data.placemarks
                    // Take only points
                    .filterIsInstance<map.placemark.Point>()
                    // Sort from top to bottom to display ordered
                    .sortedByDescending { it.longitude }
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
        mapUUID = uuidBuilder.toString()
        staticMap = builder
    }

    // Once the map request is ready, load it
    LaunchedEffect(staticMap) {
        snapshotFlow { staticMap }
            .distinctUntilChanged()
            .filterNotNull()
            .collect { map ->
                withContext(Dispatchers.IO) {
                    loadingProgress = null
                    val imageUrl = map.url().toString()
                    val imageUid = mapUUID!!.hashCode().toString()
                    val mapsDir = File(storageProvider.cacheDirectory, "maps")
                        // Create the directory if it doesn't exist
                        .also { if (!it.exists()) it.mkdirs() }
                    val imageFile = File(mapsDir, imageUid)
                    if (imageFile.exists()) {
                        Napier.d { "Image file for map is already cached: $imageFile" }
                        mapImage = imageFile.readAllBytes()
                    } else {
                        Napier.v { "Fetching map image ($imageUid): $imageUrl" }
                        val result = httpClient.get(imageUrl) {
                            onDownload { bytesSentTotal, contentLength ->
                                Napier.v { "Map image download progress: $bytesSentTotal / $contentLength" }
                                loadingProgress = (bytesSentTotal.toDouble() / contentLength.toDouble()).toFloat()
                            }
                        }
                        if (result.status.value in 200..299) {
                            val bytes = result.bodyAsChannel().toByteArray()
                            mapImage = bytes
                            imageFile.write(bytes)
                        } else {
                            Napier.e { "Could not load map image. Status: ${result.status}" }
                            // TODO: Notify user
                        }
                    }
                    loadingProgress = null
                }
            }
    }

    Box(
        modifier = modifier.onGloballyPositioned { layoutCoordinates ->
            layoutSize = layoutCoordinates.size
        },
        contentAlignment = Alignment.Center
    ) {
        mapImage?.let {
            Image(
                bitmap = it.decodeImage(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: loadingProgress?.let { CircularProgressIndicator(it) } ?: CircularProgressIndicator()
    }

    // TODO : Platform Map
}
