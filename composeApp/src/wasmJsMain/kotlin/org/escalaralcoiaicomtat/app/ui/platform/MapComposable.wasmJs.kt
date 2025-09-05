package org.escalaralcoiaicomtat.app.ui.platform

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.ui.MapUI
import kotlin.uuid.Uuid

@Composable
actual fun MapComposable(
    viewModel: MapViewModel,
    modifier: Modifier,
    kmz: Uuid?,
    blockInteractions: Boolean,
    onMapClick: (() -> Unit)?,
) {
    val mapState by viewModel.state.collectAsState()

    var layoutSize: IntSize? by remember { mutableStateOf(null) }
    LaunchedEffect(layoutSize, kmz) {
        if (layoutSize == null) return@LaunchedEffect
        if (kmz == null) return@LaunchedEffect

        viewModel.loadMap(kmz, blockInteractions, onMapClick)
    }

    // TODO: Fulfil OpenStreetMap's requirements: https://operations.osmfoundation.org/policies/tiles/

    AnimatedContent(
        mapState,
        modifier = modifier.onGloballyPositioned { layoutSize = it.size }
    ) { state ->
        if (state == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Scale: ${state.scale}",
                    modifier = Modifier.zIndex(10f).align(Alignment.TopEnd)
                )

                MapUI(
                    modifier = Modifier.fillMaxSize(),
                    state = state
                )
            }
        }
    }
}
