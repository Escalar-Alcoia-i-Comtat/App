package ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
expect fun MapComposable(
    viewModel: MapViewModel = viewModel { MapViewModel() },
    modifier: Modifier = Modifier,
    kmzUUID: String? = null
)
