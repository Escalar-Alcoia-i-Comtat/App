package platform

import org.escalaralcoiaicomtat.app.ui.navigation.Destination

actual fun <T : Destination> onNavigate(destination: T) {
    // nothing
}

actual fun <T : Destination> initialDestination(destination: T) {
    // nothing
}
