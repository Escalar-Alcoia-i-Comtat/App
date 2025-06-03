package org.escalaralcoiaicomtat.app

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import io.github.aakira.napier.Napier
import kotlinx.serialization.SerializationException
import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.data.DataTypes
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.network.ConnectivityStatusObserver
import org.escalaralcoiaicomtat.app.platform.Updates
import org.escalaralcoiaicomtat.app.ui.composition.LocalAnimatedContentScope
import org.escalaralcoiaicomtat.app.ui.composition.LocalSharedTransitionScope
import org.escalaralcoiaicomtat.app.ui.dialog.UpdateAvailableDialog
import org.escalaralcoiaicomtat.app.ui.dialog.UpdateErrorDialog
import org.escalaralcoiaicomtat.app.ui.navigation.Destination
import org.escalaralcoiaicomtat.app.ui.navigation.Destinations
import org.escalaralcoiaicomtat.app.ui.navigation.navigateTo
import org.escalaralcoiaicomtat.app.ui.screen.AppScreen
import org.escalaralcoiaicomtat.app.ui.screen.EditorScreen
import org.escalaralcoiaicomtat.app.ui.screen.IntroScreen
import org.escalaralcoiaicomtat.app.ui.screen.MapScreen
import org.escalaralcoiaicomtat.app.ui.screen.PathsScreen
import org.escalaralcoiaicomtat.app.ui.screen.ReportScreen
import org.escalaralcoiaicomtat.app.ui.screen.SectorsScreen
import org.escalaralcoiaicomtat.app.ui.screen.ZonesScreen
import org.escalaralcoiaicomtat.app.ui.theme.AppTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppRoot(
    navController: NavHostController = rememberNavController(),
    startDestination: Destination? = null,
    modifier: Modifier = Modifier
) {
    ConnectivityStatusObserver()

    val shownIntro = remember { settings.getBoolean(SettingsKeys.SHOWN_INTRO, false) }

    AppTheme {
        val updateAvailable by Updates.updateAvailable.collectAsState()
        val latestVersion by Updates.latestVersion.collectAsState()
        val updateError by Updates.updateError.collectAsState()
        if (updateAvailable) {
            UpdateAvailableDialog(latestVersion) {
                Updates.updateAvailable.tryEmit(false)
            }
        }
        updateError?.let {
            UpdateErrorDialog(it) { Updates.updateError.tryEmit(null) }
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

    val apiKey by settings.getStringOrNullFlow(SettingsKeys.API_KEY).collectAsState(null)
    val editAllowed = apiKey != null

    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
        NavHost(
            navController = navController,
            startDestination = initial,
            // → when popping the back stack, scale the outgoing screen down slightly
            popExitTransition = {
                scaleOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    ),
                    targetScale = 0.92f,
                    transformOrigin = TransformOrigin.Center
                ) + fadeOut(
                    animationSpec = tween(200)
                )
            },
            // → when the previous screen re‐enters, slide it in from the left + fade in
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(250)
                )
            },
            modifier = modifier,
        ) {
            composable<Destinations.Root> {
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    AppScreen(
                        onAreaRequested = { areaId ->
                            navController.navigateTo(Destinations.Area(areaId))
                        },
                        onZoneRequested = { parentAreaId, zoneId ->
                            navController.navigateTo(Destinations.Zone(parentAreaId, zoneId))
                        },
                        onSectorRequested = { parentAreaId, parentZoneId, sectorId, pathId ->
                            navController.navigateTo(
                                Destinations.Sector(
                                    parentAreaId,
                                    parentZoneId,
                                    sectorId,
                                    pathId
                                )
                            )
                        },
                        onEditRequested = { area: Area ->
                            navController.navigateTo(Destinations.Editor(DataTypes.Area, area.id))
                        }.takeIf { editAllowed },
                        onCreateAreaRequested = {
                            navController.navigateTo(Destinations.Editor(DataTypes.Area, null))
                        }.takeIf { editAllowed },
                        onNavigateToIntroRequested = {
                            navController.navigateTo(Destinations.Intro)
                        },
                        scrollToId = initial.id
                    )
                }
            }
            composable<Destinations.Intro> {
                IntroScreen(
                    onIntroFinished = {
                        navController.navigateTo(Destinations.Root)
                    }
                )
            }
            composable<Destinations.Area> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Area>()

                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    ZonesScreen(
                        areaId = route.areaId,
                        onBackRequested = { navController.navigateUp() },
                        onZoneRequested = { navController.navigateTo(route.down(it)) },
                        onEditAreaRequested = {
                            navController.navigateTo(Destinations.Editor(DataTypes.Area, route.id))
                        }.takeIf { editAllowed },
                        onEditZoneRequested = { zone: Zone ->
                            navController.navigateTo(Destinations.Editor(DataTypes.Zone, zone.id))
                        }.takeIf { editAllowed },
                        onCreateZoneRequested = {
                            navController.navigateTo(
                                Destinations.Editor(DataTypes.Zone, null, route.id)
                            )
                        }.takeIf { editAllowed },
                    )
                }
            }
            composable<Destinations.Zone> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Zone>()

                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    SectorsScreen(
                        zoneId = route.zoneId,
                        editAllowed = editAllowed,
                        onBackRequested = { navController.navigateUp() },
                        onSectorRequested = { navController.navigateTo(route.down(it)) },
                        onEditZoneRequested = {
                            navController.navigateTo(Destinations.Editor(DataTypes.Zone, route.id))
                        }.takeIf { editAllowed },
                        onEditSectorRequested = { sector: Sector ->
                            navController.navigateTo(
                                Destinations.Editor(
                                    DataTypes.Sector,
                                    sector.id
                                )
                            )
                        }.takeIf { editAllowed },
                        onCreateSectorRequested = {
                            navController.navigateTo(
                                Destinations.Editor(DataTypes.Sector, null, route.id)
                            )
                        }.takeIf { editAllowed },
                        onMapClicked = {
                            navController.navigateTo(Destinations.Map(it))
                        },
                    )
                }
            }
            composable<Destinations.Sector> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Sector>()
                val previousRoute = try {
                    navController.previousBackStackEntry?.toRoute<Destinations.Zone>()
                } catch (e: SerializationException) {
                    Napier.e(e) { "Could not decode destination." }
                    null
                }

                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    PathsScreen(
                        sectorId = route.sectorId,
                        highlightPathId = route.pathId,
                        onBackRequested = { navController.navigateUp() },
                        onReportRequested = { path ->
                            navController.navigateTo(
                                Destinations.Report(
                                    sectorId = route.sectorId.takeIf { path == null },
                                    pathId = path?.id,
                                )
                            )
                        },
                        onEditSectorRequested = {
                            navController.navigateTo(
                                Destinations.Editor(DataTypes.Sector, route.id, route.parentZoneId)
                            )
                        }.takeIf { editAllowed },
                        onEditPathRequested = { path: Path ->
                            Napier.i { "Editing path ${path.id}" }
                            navController.navigateTo(
                                Destinations.Editor(DataTypes.Path, path.id, route.sectorId)
                            )
                        }.takeIf { editAllowed },
                        onCreatePathRequested = {
                            navController.navigateTo(
                                Destinations.Editor(DataTypes.Path, null, route.id)
                            )
                        }.takeIf { editAllowed },
                        onNextSectorRequested = { id ->
                            navController.navigateTo(
                                Destinations.Sector(route.parentAreaId, route.parentZoneId, id),
                                popUpTo = previousRoute,
                            )
                        },
                        onPreviousSectorRequested = { id ->
                            navController.navigateTo(
                                Destinations.Sector(route.parentAreaId, route.parentZoneId, id),
                                popUpTo = previousRoute,
                            )
                        }
                    )
                }
            }
            composable<Destinations.Editor> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Editor>()
                val dataTypes = remember(route) { DataTypes.Companion.valueOf(route.dataTypes) }

                EditorScreen(
                    dataTypes,
                    route.id,
                    route.parentId,
                    onBackRequested = navController::navigateUp,
                    afterDelete = {
                        val backStackEntry = navController.previousBackStackEntry
                        if (route.parentId == null || backStackEntry == null) {
                            navController.navigateTo(Destinations.Root, true)
                            return@EditorScreen
                        }
                        val previousDestination = backStackEntry.toRoute<Destination>()
                        // If popBackStack returns null, just navigate to root as single top
                        if (!navController.popBackStack(previousDestination, false)) {
                            navController.navigateTo(Destinations.Root, true)
                        }
                    },
                )
            }
            composable<Destinations.Map> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Map>()

                MapScreen(
                    kmz = route.kmz,
                    onBackRequested = { navController.navigateUp() }
                )
            }
            composable<Destinations.Report> { navBackStackEntry ->
                val route = navBackStackEntry.toRoute<Destinations.Report>()

                if (route.isNull()) {
                    navController.navigateUp()
                    return@composable
                }

                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    ReportScreen(
                        sectorId = route.sectorId,
                        pathId = route.pathId,
                        onBackRequested = navController::navigateUp,
                    )
                }
            }
        }
    }
}
