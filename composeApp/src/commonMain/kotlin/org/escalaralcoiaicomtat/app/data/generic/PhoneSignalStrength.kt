package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet4Bar
import androidx.compose.material.icons.filled.SignalCellularOff
import androidx.compose.material.icons.filled._3gMobiledata
import androidx.compose.material.icons.filled._4gMobiledata
import androidx.compose.material.icons.filled._5g
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
enum class PhoneSignalStrength(
    val icon: ImageVector
) {
    NOT_AVAILABLE(Icons.Default.SignalCellularOff),
    BAD_SIGNAL(Icons.Default.SignalCellularConnectedNoInternet4Bar),
    SIGNAL_3G(Icons.Default._3gMobiledata),
    SIGNAL_4G(Icons.Default._4gMobiledata),
    SIGNAL_5G(Icons.Default._5g),
}
