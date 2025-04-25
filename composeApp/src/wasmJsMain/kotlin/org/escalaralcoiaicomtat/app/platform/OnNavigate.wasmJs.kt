package org.escalaralcoiaicomtat.app.platform

import org.escalaralcoiaicomtat.app.ui.navigation.Destination

// TODO: Enable history pushing when a solution is found for relative URL loading
// String resources are sometimes loaded asynchronously, so they are not found on lower paths

actual fun <T : Destination> onNavigate(destination: T) {
    // window.history.pushState(destination.name.toJsString(), destination.name, destination.path)
}

actual fun <T : Destination> initialDestination(destination: T) {
    // window.history.replaceState(destination.name.toJsString(), destination.name, destination.path)
}
