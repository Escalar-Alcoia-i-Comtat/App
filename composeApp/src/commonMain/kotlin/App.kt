import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.SupervisorJob
import network.ConnectivityStatusObserver
import platform.Updates
import ui.composition.LocalNavController
import ui.dialog.UpdateAvailableDialog
import ui.navigation.Routes
import ui.screen.AppScreen
import ui.screen.IntroScreen
import ui.screen.PathsScreen
import ui.screen.SectorsScreen
import ui.screen.ZonesScreen
import ui.theme.AppTheme
import utils.createStore

val store = CoroutineScope(SupervisorJob()).createStore()

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    initial: Pair<EDataType, Long>? = null
) {
    ConnectivityStatusObserver()

    val shownIntro = remember { settings.getBoolean(SettingsKeys.SHOWN_INTRO, false) }

    AppTheme {
        val updateAvailable by Updates.updateAvailable.collectAsState()
        val latestVersion by Updates.latestVersion.collectAsState()
        if (updateAvailable) {
            UpdateAvailableDialog(latestVersion) {
                Updates.updateAvailable.tryEmit(false)
            }
        }

        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(
                navController = LocalNavController.current!!,
                startDestination = if (shownIntro) Routes.ROOT else Routes.INTRO
            ) {
                composable(Routes.ROOT) {
                    AppScreen(initial = initial)
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
                    } else {
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
                    } else {
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
                    } else {
                        PathsScreen(sectorId, pathId)
                    }
                }
            }
        }
    }
}
