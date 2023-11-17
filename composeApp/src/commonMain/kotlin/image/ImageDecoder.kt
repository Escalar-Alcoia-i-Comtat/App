package image

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Encodes the [ByteArray] into a Bitmap to display using Jetpack Compose.
 */
expect fun ByteArray.decodeImage(): ImageBitmap
