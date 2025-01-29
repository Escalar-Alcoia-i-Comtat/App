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
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import data.EDataType
import database.SettingsKeys
import database.settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import network.ConnectivityStatusObserver
import platform.Updates
import sync.DataSync
import ui.composition.LocalAnimatedContentScope
import ui.composition.LocalSharedTransitionScope
import ui.dialog.UpdateAvailableDialog
import ui.navigation.Destinations
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
    initial: EDataType? = null,
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
                initial = initial,
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
    initial: EDataType? = null
) {
    val startDestination = remember(shownIntro, initial) {
        if (!shownIntro) {
            Destinations.Intro
        } else if (initial == null) {
            Destinations.Root
        } else {
            when (initial) {
                is EDataType.Area -> Destinations.Area(initial.id)
                is EDataType.Zone -> Destinations.Zone(initial.id)
                is EDataType.Sector -> Destinations.Sector(initial.id)
                is EDataType.Path -> Destinations.Sector(initial.sectorId, initial.id)
            }
        }
    }

    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable<Destinations.Root> {
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    AppScreen(
                        onAreaRequested = { areaId ->
                            navController.navigate(Destinations.Area(areaId))
                        },
                        onZoneRequested = { zoneId ->
                            navController.navigate(Destinations.Zone(zoneId))
                        },
                        onSectorRequested = { sectorId, pathId ->
                            navController.navigate(Destinations.Sector(sectorId, pathId))
                        },
                        scrollToId = initial?.id
                    )
                }
            }
            composable<Destinations.Intro> {
                IntroScreen(
                    onIntroFinished = {
                        navController.navigate(
                            Destinations.Root,
                            NavOptions.Builder().setLaunchSingleTop(true).build()
                        )
                    }
                )
            }
            composable<Destinations.Area> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Area>()
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    ZonesScreen(
                        areaId = route.areaId,
                        onBackRequested = { navController.navigateUp() },
                        onZoneRequested = { navController.navigate(Destinations.Zone(it)) }
                    )
                }
            }
            composable<Destinations.Zone> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Zone>()
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    SectorsScreen(
                        zoneId = route.zoneId,
                        onBackRequested = { navController.navigateUp() },
                        onSectorRequested = { navController.navigate(Destinations.Sector(it)) }
                    )
                }
            }
            composable<Destinations.Sector> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Sector>()
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    PathsScreen(
                        sectorId = route.sectorId,
                        highlightPathId = route.pathId,
                        onBackRequested = { navController.navigateUp() },
                    )
                }
            }
        }
    }
}
