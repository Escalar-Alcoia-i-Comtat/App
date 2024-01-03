package ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi

@Composable
@OptIn(MapsComposeExperimentalApi::class)
actual fun MapComposable(modifier: Modifier) {
    GoogleMap(
        modifier = modifier
    ) {
        MapEffect { googleMap ->

        }
    }
}
