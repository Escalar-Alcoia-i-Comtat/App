package platform

import ui.navigation.Destination

expect fun <T: Destination> onNavigate(destination: T, isSingleTop: Boolean = false)

expect fun <T: Destination> initialDestination(destination: T)
