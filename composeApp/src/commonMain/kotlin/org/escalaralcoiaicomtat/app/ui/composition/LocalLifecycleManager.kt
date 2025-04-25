package org.escalaralcoiaicomtat.app.ui.composition

import androidx.compose.runtime.compositionLocalOf
import org.escalaralcoiaicomtat.app.platform.LifecycleManager

val LocalLifecycleManager = compositionLocalOf { LifecycleManager() }
