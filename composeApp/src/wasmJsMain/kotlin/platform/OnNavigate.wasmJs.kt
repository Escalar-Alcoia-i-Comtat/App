package platform

import kotlinx.browser.window
import ui.navigation.Destination

actual fun <T : Destination> onNavigate(destination: T) {
    window.history.pushState(destination.name.toJsString(), destination.name, destination.path)
}

actual fun <T : Destination> initialDestination(destination: T) {
    window.history.replaceState(destination.name.toJsString(), destination.name, destination.path)
}
