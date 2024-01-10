package ui.platform

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.mxalbert.zoomable.Zoomable

@Composable
actual fun ZoomableImage(
    image: ImageBitmap?,
    modifier: Modifier,
    contentDescription: String?
) {
    Zoomable(
        modifier = modifier
    ) {
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
