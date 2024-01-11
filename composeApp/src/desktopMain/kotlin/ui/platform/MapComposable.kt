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
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.StaticMapCriteria
import com.mapbox.geojson.Point
import image.decodeImage
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import network.createHttpClient

private val httpClient = createHttpClient()

@Composable
actual fun MapComposable(modifier: Modifier, kmzUUID: String?) {
    var layoutSize: IntSize? by remember { mutableStateOf(null) }
    var staticMap: MapboxStaticMap? by remember { mutableStateOf(null) }
    var mapImage: ByteArray? by remember { mutableStateOf(null) }
    var loadingProgress: Float? by remember { mutableStateOf(null) }

    // Instantiate the map when layout is positioned
    LaunchedEffect(layoutSize) {
        if (layoutSize == null) return@LaunchedEffect
        val size = layoutSize!!

        staticMap = MapboxStaticMap.builder()
            .accessToken(BuildKonfig.MAPBOX_ACCESS_TOKEN)
            .styleId(StaticMapCriteria.LIGHT_STYLE)
            .cameraPoint(Point.fromLngLat(-0.455466, 38.7326039))
            .cameraZoom(8.0)
            .width(size.width)
            .height(size.height)
            .build()
    }

    LaunchedEffect(staticMap) {
        snapshotFlow { staticMap }
            .distinctUntilChanged()
            .filterNotNull()
            .collect { map ->
                withContext(Dispatchers.IO) {
                    loadingProgress = null
                    val imageUrl = map.url().toString()
                    Napier.v { "Fetching map image: $imageUrl" }
                    val result = httpClient.get(imageUrl) {
                        onDownload { bytesSentTotal, contentLength ->
                            Napier.v { "Map image download progress: $bytesSentTotal / $contentLength" }
                            loadingProgress = (bytesSentTotal.toDouble() / contentLength.toDouble()).toFloat()
                        }
                    }
                    mapImage = result.bodyAsChannel().toByteArray()
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
