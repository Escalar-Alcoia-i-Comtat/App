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
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import data.Area
import data.DataTypes
import data.Sector
import data.Zone
import database.SettingsKeys
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
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
import ui.screen.EditorScreen
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
            val lastSync = settings
                .getLongOrNull(SettingsKeys.LAST_SYNC_TIME)
                ?.let { Instant.fromEpochMilliseconds(it) }
            val now = Clock.System.now()

            // Synchronize if never synced, or every 12 hours
            if (lastSync == null || (now - lastSync).inWholeHours > 12) {
                DataSync.start(DataSync.Cause.Scheduled)
            } else {
                Napier.d { "Won't run synchronization. Last run: ${(now - lastSync).inWholeHours} hours ago" }
            }
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
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSettingsApi::class)
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

    val apiKey by settings.getStringOrNullFlow(SettingsKeys.API_KEY).collectAsState(null)
    val editAllowed = apiKey != null

    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
        NavHost(
            navController = navController,
            startDestination = initial,
            modifier = modifier,
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
                        onEditRequested = { area: Area ->
                            navController.navigateTo(Destinations.Editor(DataTypes.Area, area.id))
                        }.takeIf { editAllowed },
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
                        onZoneRequested = { navController.navigateTo(route.down(it)) },
                        onEditRequested = { zone: Zone ->
                            navController.navigateTo(Destinations.Editor(DataTypes.Zone, zone.id))
                        }.takeIf { editAllowed }
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
                        onSectorRequested = { navController.navigateTo(route.down(it)) },
                        onEditRequested = { sector: Sector ->
                            navController.navigateTo(Destinations.Editor(DataTypes.Sector, sector.id))
                        }.takeIf { editAllowed }
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
            composable<Destinations.Editor> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Editor>()
                LaunchedEffect(Unit) { onNavigate(route) }
                val dataTypes = remember(route) { DataTypes.valueOf(route.dataTypes) }

                EditorScreen(dataTypes, route.id) { navController.navigateUp() }
            }
        }
    }
}
