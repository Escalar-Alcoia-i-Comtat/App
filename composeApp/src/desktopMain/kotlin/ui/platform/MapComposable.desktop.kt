package ui.platform

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import build.BuildKonfig
import org.jetbrains.compose.resources.decodeToImageBitmap

@Composable
actual fun MapComposable(viewModel: MapViewModel, modifier: Modifier, kmzUUID: String?) {
    // Require that the Mapbox access token is set
    BuildKonfig.MAPBOX_ACCESS_TOKEN ?: return

    var layoutSize: IntSize? by remember { mutableStateOf(null) }
    val mapImage: ByteArray? by viewModel.mapImage.collectAsState()
    val loadingProgress by viewModel.progress.collectAsState()

    // Instantiate the map when layout is positioned
    LaunchedEffect(layoutSize, kmzUUID) {
        if (layoutSize == null) return@LaunchedEffect
        if (kmzUUID == null) return@LaunchedEffect

        viewModel.loadMap(kmzUUID, layoutSize!!)
    }

    AnimatedContent(
        targetState = mapImage to loadingProgress,
        modifier = modifier
    ) { (mapImage, loadingProgress) ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { layoutSize = it.size },
            contentAlignment = Alignment.Center
        ) {
            if (mapImage != null) {
                Image(
                    bitmap = mapImage.decodeToImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (loadingProgress != null) {
                CircularProgressIndicator(progress = { loadingProgress })
            } else {
                CircularProgressIndicator()
            }
        }
    }
}