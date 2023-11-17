package image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

/**
 * Decodes the [ByteArray] into a Bitmap to display using Jetpack Compose.
 */
actual fun ByteArray.decodeImage(): ImageBitmap {
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}
