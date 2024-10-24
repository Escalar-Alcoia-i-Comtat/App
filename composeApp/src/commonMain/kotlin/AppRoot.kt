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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import ui.composition.LocalNavController
import ui.composition.LocalSharedTransitionScope
import ui.dialog.UpdateAvailableDialog
import ui.navigation.Routes
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
            Routes.INTRO
        } else if (initial == null) {
            Routes.ROOT
        } else {
            when (initial) {
                is EDataType.Area -> Routes.area(initial.id)
                is EDataType.Zone -> Routes.zone(initial.id)
                is EDataType.Sector -> Routes.sector(initial.id)
                is EDataType.Path -> Routes.sector(initial.sectorId, initial.id)
            }
        }
    }

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSharedTransitionScope provides this
    ) {
        NavHost(
            navController = LocalNavController.current!!,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable(Routes.ROOT) {
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    AppScreen(scrollToId = initial?.id)
                }
            }
            composable(Routes.INTRO) {
                IntroScreen()
            }
            composable(
                route = Routes.ZONES,
                arguments = listOf(
                    navArgument("areaId") { type = NavType.LongType }
                )
            ) { entry ->
                val areaId = entry.arguments?.getLong("areaId")
                if (areaId == null) {
                    navController.popBackStack()
                } else CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    ZonesScreen(areaId)
                }
            }
            composable(
                route = Routes.SECTORS,
                arguments = listOf(
                    navArgument("zoneId") { type = NavType.LongType }
                )
            ) { entry ->
                val zoneId = entry.arguments?.getLong("zoneId")
                if (zoneId == null) {
                    navController.popBackStack()
                } else CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    SectorsScreen(zoneId)
                }
            }
            composable(
                route = Routes.PATHS,
                arguments = listOf(
                    navArgument("sectorId") { type = NavType.LongType },
                    navArgument("pathId") { type = NavType.LongType }
                )
            ) { entry ->
                val sectorId = entry.arguments?.getLong("sectorId")
                val pathId = entry.arguments?.getLong("pathId")

                if (sectorId == null) {
                    navController.popBackStack()
                } else CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    PathsScreen(sectorId, pathId)
                }
            }
        }
    }
}
