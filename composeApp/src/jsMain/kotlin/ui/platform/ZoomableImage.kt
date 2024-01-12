package ui.platform

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

@Composable
actual fun ZoomableImage(
    image: ImageBitmap?,
    modifier: Modifier,
    contentDescription: String?
) {
    // TODO: Zoom support
    Box(modifier, contentAlignment = Alignment.Center) {
        image?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                modifier = Modifier
                    .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                    .fillMaxSize()
            )
        } ?: CircularProgressIndicator()
    }
}
