package ui.composition

import androidx.compose.runtime.compositionLocalOf
import utils.unit.UnitsConfiguration

val LocalUnitsConfiguration = compositionLocalOf { UnitsConfiguration() }
