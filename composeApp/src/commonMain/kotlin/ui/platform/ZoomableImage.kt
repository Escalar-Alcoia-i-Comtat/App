package ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun ZoomableImage(
    image: ImageBitmap?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
)
