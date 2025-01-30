package platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.browser.window
import org.w3c.dom.PopStateEvent
import org.w3c.dom.events.Event

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = { event ->
            if (enabled && event is PopStateEvent) {
                event.preventDefault()
                onBack()
            }
        }
        window.addEventListener("popstate", listener)

        onDispose {
            window.removeEventListener("popstate", listener)
        }
    }
}
