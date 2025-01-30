import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import database.SettingsKeys
import database.settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import network.ConnectivityStatusObserver
import platform.Updates
import platform.initialDestination
import platform.onNavigate
import sync.DataSync
import ui.composition.LocalAnimatedContentScope
import ui.composition.LocalSharedTransitionScope
import ui.dialog.UpdateAvailableDialog
import ui.navigation.Destination
import ui.navigation.Destinations
import ui.navigation.navigateTo
import ui.screen.AppScreen
import ui.screen.IntroScreen
import ui.screen.PathsScreen
import ui.screen.SectorsScreen
import ui.screen.ZonesScreen
import ui.theme.AppTheme
import utils.IO
import utils.createStore

val store = CoroutineScope(SupervisorJob()).createStore()

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppRoot(
    navController: NavHostController = rememberNavController(),
    startDestination: Destination? = null,
    modifier: Modifier = Modifier
) {
    ConnectivityStatusObserver()

    val shownIntro = remember { settings.getBoolean(SettingsKeys.SHOWN_INTRO, false) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            DataSync.start()
        }
    }

    AppTheme {
        val updateAvailable by Updates.updateAvailable.collectAsState()
        val latestVersion by Updates.latestVersion.collectAsState()
        if (updateAvailable) {
            UpdateAvailableDialog(latestVersion) {
                Updates.updateAvailable.tryEmit(false)
            }
        }

        SharedTransitionLayout(
            modifier = modifier
        ) {
            NavigationController(
                navController = navController,
                shownIntro = shownIntro,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun SharedTransitionScope.NavigationController(
    navController: NavHostController,
    shownIntro: Boolean,
    modifier: Modifier = Modifier,
    startDestination: Destination? = null
) {
    val initial = remember {
        if (!shownIntro) Destinations.Intro else startDestination ?: Destinations.Root
    }
    LaunchedEffect(initial) { initialDestination(initial) }

    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
        NavHost(
            navController = navController,
            startDestination = initial,
            modifier = modifier
        ) {
            composable<Destinations.Root> {
                LaunchedEffect(Unit) { onNavigate(Destinations.Root) }

                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    AppScreen(
                        onAreaRequested = { areaId ->
                            navController.navigateTo(Destinations.Area(areaId))
                        },
                        onZoneRequested = { parentAreaId, zoneId ->
                            navController.navigateTo(Destinations.Zone(parentAreaId, zoneId))
                        },
                        onSectorRequested = { parentAreaId, parentZoneId, sectorId, pathId ->
                            navController.navigateTo(Destinations.Sector(parentAreaId, parentZoneId, sectorId, pathId))
                        },
                        scrollToId = initial.id
                    )
                }
            }
            composable<Destinations.Intro> {
                LaunchedEffect(Unit) { onNavigate(Destinations.Intro) }

                IntroScreen(
                    onIntroFinished = {
                        navController.navigateTo(Destinations.Root)
                    }
                )
            }
            composable<Destinations.Area> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Area>()
                LaunchedEffect(Unit) { onNavigate(route) }

                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    ZonesScreen(
                        areaId = route.areaId,
                        onBackRequested = { navController.navigateTo(route.up()) },
                        onZoneRequested = { navController.navigateTo(route.down(it)) }
                    )
                }
            }
            composable<Destinations.Zone> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Zone>()
                LaunchedEffect(Unit) { onNavigate(route) }

                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    SectorsScreen(
                        zoneId = route.zoneId,
                        onBackRequested = { navController.navigateTo(route.up()) },
                        onSectorRequested = { navController.navigateTo(route.down(it)) }
                    )
                }
            }
            composable<Destinations.Sector> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Sector>()
                LaunchedEffect(Unit) { onNavigate(route) }

                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    PathsScreen(
                        sectorId = route.sectorId,
                        highlightPathId = route.pathId,
                        onBackRequested = { navController.navigateTo(route.up()) },
                    )
                }
            }
        }
    }
}
