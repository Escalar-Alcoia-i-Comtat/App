package org.escalaralcoiaicomtat.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Set to true when the user has made the "back" gesture.
 */
val backEventReceiver = MutableStateFlow(false)

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    val hasGoneBack by backEventReceiver.collectAsState(false)

    LaunchedEffect(hasGoneBack) {
        if (hasGoneBack && enabled) {
            onBack()
            // Set back to false
            synchronized(backEventReceiver) {
                if (backEventReceiver.value) {
                    backEventReceiver.tryEmit(false)
                }
            }
        }
    }
}
