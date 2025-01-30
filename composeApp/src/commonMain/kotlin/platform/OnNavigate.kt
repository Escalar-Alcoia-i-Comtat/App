package platform

import ui.navigation.Destination

expect fun <T: Destination> onNavigate(destination: T)

expect fun <T: Destination> initialDestination(destination: T)
