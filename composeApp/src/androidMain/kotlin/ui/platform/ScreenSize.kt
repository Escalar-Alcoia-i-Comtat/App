package ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
actual fun getScreenSize(): DpSize {
    val configuration = LocalConfiguration.current
    return DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)
}
