package org.escalaralcoiaicomtat.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute

/**
 * Binds the navController to the current window.
 * @param initRoute The current window hash. Usually `window.location.hash`.
 * @param bindToNavigation A reference to `window.bindToNavigation`.
 * Usually `window.bindToNavigation(navController, getBackStackEntryRoute)`
 */
@Composable
fun NavigationControllerToWindowBinder(
    initRoute: () -> String,
    bindToNavigation: suspend (getBackStackEntryRoute: ((NavBackStackEntry) -> String)?) -> Unit,
    onNavigateTo: (destination: Destination, isSingleTop: Boolean) -> Unit,
) {
    LaunchedEffect(Unit) {
        val initRoute = initRoute()
        val initDestination = Destinations.parse(initRoute)
        onNavigateTo(initDestination, true)

        bindToNavigation { entry ->
            val route = entry.destination.route.orEmpty()
            when {
                route.startsWith(Destinations.Root.serializer().descriptor.serialName) -> {
                    "#/root"
                }
                route.startsWith(Destinations.Intro.serializer().descriptor.serialName) -> {
                    "#/intro"
                }
                route.startsWith(Destinations.Area.serializer().descriptor.serialName) -> {
                    val args = entry.toRoute<Destinations.Area>()
                    "#/${args.areaId}"
                }
                route.startsWith(Destinations.Zone.serializer().descriptor.serialName) -> {
                    val args = entry.toRoute<Destinations.Zone>()
                    "#/${args.parentAreaId}/${args.zoneId}"
                }
                route.startsWith(Destinations.Sector.serializer().descriptor.serialName) -> {
                    val args = entry.toRoute<Destinations.Sector>()
                    "#/${args.parentAreaId}/${args.parentZoneId}/${args.sectorId}${args.pathId?.let { "/$it" } ?: ""}"
                }
                route.startsWith(Destinations.Editor.serializer().descriptor.serialName) -> {
                    val args = entry.toRoute<Destinations.Editor>()
                    "#/edit/${args.dataTypes}${args.id?.let { "/$it" } ?: "/new"}"
                }
                // Doesn't set a URL fragment for all other routes
                else -> ""
            }
        }
    }
}
