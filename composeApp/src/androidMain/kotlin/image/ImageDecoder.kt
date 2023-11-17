package image

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Encodes the [ByteArray] into a Bitmap to display using Jetpack Compose.
 */
actual fun ByteArray.decodeImage(): ImageBitmap {
    return BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()
}
