package platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import kotlinx.browser.window
import org.w3c.dom.PopStateEvent

@Composable
actual fun PlatformNavHandler(navHandler: NavController) {
    LaunchedEffect(Unit) {
        window.addEventListener("popstate") { event ->
            if (event !is PopStateEvent) return@addEventListener

            navHandler.navigateUp()
        }
    }
}
