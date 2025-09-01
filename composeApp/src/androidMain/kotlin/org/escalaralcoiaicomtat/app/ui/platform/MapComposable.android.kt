package org.escalaralcoiaicomtat.app.ui.platform

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Composable
@OptIn(MapsComposeExperimentalApi::class)
@SuppressLint("PotentialBehaviorOverride")
actual fun MapComposable(
    viewModel: MapViewModel,
    modifier: Modifier,
    kmz: Uuid?,
    blockInteractions: Boolean,
    onMapClick: (() -> Unit)?,
) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState()

    val isLoading by viewModel.isLoading.collectAsState()
    val mapError by viewModel.mapError.collectAsState()

    Box(modifier, contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator()
        }

        if (onMapClick != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f)
                    .clickable(onClick = onMapClick)
            )
        }

        mapError?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = error.icon,
                        contentDescription = stringResource(error.message),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = stringResource(error.message),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        } ?: GoogleMap(
            cameraPositionState = cameraPositionState,
            modifier = Modifier.fillMaxSize(),
            uiSettings = MapUiSettings(
                compassEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = !blockInteractions,
                scrollGesturesEnabled = !blockInteractions,
                scrollGesturesEnabledDuringRotateOrZoom = !blockInteractions,
                tiltGesturesEnabled = false,
                zoomGesturesEnabled = !blockInteractions,
                zoomControlsEnabled = false
            )
        ) {
            MapEffect(Unit) {
                if (blockInteractions) {
                    // Skip default marker events
                    it.setOnMarkerClickListener { true }
                }
            }

            MapEffect(kmz) { googleMap ->
                if (kmz != null) {
                    // Remove previous KML
                    viewModel.disposeLayer()

                    // Load new KML
                    viewModel.load(context, googleMap, kmz, cameraPositionState::move)
                }
            }
        }
    }
}
