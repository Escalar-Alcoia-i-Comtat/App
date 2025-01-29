package platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.window
import org.w3c.dom.PopStateEvent

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    LaunchedEffect(Unit) {
        window.addEventListener("popstate") { event ->
            if (!enabled) return@addEventListener
            if (event !is PopStateEvent) return@addEventListener

            onBack()
        }
    }
}
