package ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.input.key.KeyEvent
import ui.state.KeyEventCollector

@Composable
fun LaunchedKeyEvent(onKeyEvent: (KeyEvent) -> Boolean) {
    DisposableEffect(Unit) {
        val collector = KeyEventCollector { onKeyEvent(it) }
        KeyEventCollector.startCollecting(collector)
        onDispose { KeyEventCollector.stopCollecting(collector) }
    }
}
