package org.escalaralcoiaicomtat.app.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.uuid.Uuid

@Composable
actual fun MapComposable(
    viewModel: MapViewModel,
    modifier: Modifier,
    kmz: Uuid?,
    blockInteractions: Boolean,
    onMapClick: (() -> Unit)?,
) {
    // TODO: Not yet implemented
}
