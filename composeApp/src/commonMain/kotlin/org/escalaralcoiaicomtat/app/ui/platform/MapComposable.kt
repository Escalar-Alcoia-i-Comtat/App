package org.escalaralcoiaicomtat.app.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.uuid.Uuid

@Composable
expect fun MapComposable(
    viewModel: MapViewModel = viewModel { MapViewModel() },
    modifier: Modifier = Modifier,
    kmz: Uuid? = null,
)
