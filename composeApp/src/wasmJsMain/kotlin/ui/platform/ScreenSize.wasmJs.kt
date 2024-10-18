package ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import kotlinx.browser.window

@Composable
actual fun getScreenSize(): DpSize {
    val (width, height) = window.innerWidth to window.innerHeight
    val density = LocalDensity.current
    return with(density) { DpSize(width.toDp(), height.toDp()) }
}
