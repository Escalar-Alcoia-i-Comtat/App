package org.escalaralcoiaicomtat.app.platform

import org.escalaralcoiaicomtat.app.ui.navigation.Destination

expect fun <T: Destination> onNavigate(destination: T)

expect fun <T: Destination> initialDestination(destination: T)
