package ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun getScreenSize(): DpSize {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val width = with(density) { windowInfo.containerSize.height.toDp() }
    val height = with(density) { windowInfo.containerSize.height.toDp() }
    return DpSize(width, height)
}
