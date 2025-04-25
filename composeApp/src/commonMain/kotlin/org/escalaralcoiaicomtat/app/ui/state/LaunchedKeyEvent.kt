package org.escalaralcoiaicomtat.app.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.input.key.KeyEvent

@Composable
fun LaunchedKeyEvent(onKeyEvent: (KeyEvent) -> Boolean) {
    DisposableEffect(Unit) {
        val collector = KeyEventCollector { onKeyEvent(it) }
        KeyEventCollector.startCollecting(collector)
        onDispose { KeyEventCollector.stopCollecting(collector) }
    }
}
