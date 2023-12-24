package ui.composition

import androidx.compose.runtime.compositionLocalOf
import platform.LifecycleManager

val LocalLifecycleManager = compositionLocalOf { LifecycleManager() }
