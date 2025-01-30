package platform

import ui.navigation.Destination

actual fun <T : Destination> onNavigate(destination: T) {
    // nothing
}

actual fun <T : Destination> initialDestination(destination: T) {
    // nothing
}
