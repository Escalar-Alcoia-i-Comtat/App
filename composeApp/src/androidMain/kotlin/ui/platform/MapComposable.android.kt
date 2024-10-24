package ui.platform

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
@OptIn(MapsComposeExperimentalApi::class)
@SuppressLint("PotentialBehaviorOverride")
actual fun MapComposable(
    viewModel: MapViewModel,
    modifier: Modifier,
    kmzUUID: String?
) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState()

    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier, contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator()
        }

        GoogleMap(
            cameraPositionState = cameraPositionState,
            modifier = Modifier.fillMaxSize(),
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
                    viewModel.disposeLayer()

                    // Load new KML
                    viewModel.load(context, googleMap, kmzUUID, cameraPositionState::move)
                }
            }
        }
    }
}
