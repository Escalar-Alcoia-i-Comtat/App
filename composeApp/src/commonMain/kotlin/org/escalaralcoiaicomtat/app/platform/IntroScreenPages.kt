package org.escalaralcoiaicomtat.app.platform

import androidx.compose.runtime.Composable

/**
 * Specific pages for the current platform.
 */
expect object IntroScreenPages {
    val pages: Array<@Composable () -> Unit>
}
