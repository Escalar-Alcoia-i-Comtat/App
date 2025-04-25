package org.escalaralcoiaicomtat.app.ui.composition

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

@ExperimentalSharedTransitionApi
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
