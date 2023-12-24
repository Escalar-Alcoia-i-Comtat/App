package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransition
import data.EDataType
import database.Area
import database.Path
import database.Sector
import database.Zone
import database.database
import dev.icerock.moko.resources.compose.stringResource
import network.connectivityStatus
import resources.MR
import search.Filter
import ui.composition.LocalLifecycleManager
import ui.dialog.SearchFiltersDialog
import ui.model.AppScreenModel
import ui.model.SearchModel
import ui.navigation.AdaptiveNavigationScaffold
import ui.navigation.NavigationItem
import ui.state.collectAsStateList
import utils.unaccent

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3Api::class
)
class AppScreen(
    private val initial: Pair<EDataType, Long>? = null
) : Screen {
    @Composable
    override fun Content() {
        val lifecycleManager = LocalLifecycleManager.current
        val isNetworkConnected by connectivityStatus.isNetworkConnected.collectAsState()

        val areas by database.areaQueries.getAll().collectAsStateList()
        val zones by database.zoneQueries.getAll().collectAsStateList()
        val sectors by database.sectorQueries.getAll().collectAsStateList()
        val paths by database.pathQueries.getAll().collectAsStateList()

        val searchModel = rememberScreenModel { SearchModel() }

        Navigator(
            screen = when (initial?.first) {
                EDataType.AREA -> ZonesScreen(initial.second)
                EDataType.ZONE -> SectorsScreen(initial.second)
                EDataType.SECTOR -> PathsScreen(initial.second)
                // TODO: highlight paths
                EDataType.PATH -> PathsScreen(initial.second)
                else -> MainScreen
            }
        ) { navigator ->
            val model = navigator.rememberNavigatorScreenModel { AppScreenModel() }

            val screen = navigator.lastItem
            val depth = (screen as? DepthScreen)?.depth
            val isRoot = depth?.let { it <= 0 } ?: true

            val selection by model.selection.collectAsState()

            AdaptiveNavigationScaffold(
                items = listOf(
                    NavigationItem(
                        label = { stringResource(MR.strings.navigation_explore) },
                        icon = { Icons.Outlined.Explore }
                    ),
                    NavigationItem(
                        label = { stringResource(MR.strings.navigation_settings) },
                        icon = { Icons.Outlined.Settings }
                    )
                ),
                userScrollEnabled = isRoot,
                navigationBarVisible = isRoot,
                topBar = {
                    val isSearching by searchModel.isSearching.collectAsState(false)

                    AnimatedContent(
                        targetState = isSearching
                    ) { searching ->
                        if (searching) {
                            SearchBarLogic(areas, zones, sectors, paths, searchModel, navigator)
                        } else {
                            CenterAlignedTopAppBar(
                                title = {
                                    AnimatedContent(
                                        targetState = selection,
                                        transitionSpec = {
                                            val enter = (slideInVertically { -it } + fadeIn())
                                            val exit = (slideOutVertically { -it } + fadeOut())
                                            enter togetherWith exit
                                        }
                                    ) { dataType ->
                                        Text(
                                            text = when {
                                                dataType != null -> dataType.displayName
                                                else -> "Escalar Alcoià i Comtat"
                                            }
                                        )
                                    }
                                },
                                navigationIcon = {
                                    AnimatedVisibility(
                                        visible = !isRoot,
                                        enter = slideInHorizontally { -it },
                                        exit = slideOutHorizontally { -it }
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (navigator.size <= 1) {
                                                    lifecycleManager.finish()
                                                } else {
                                                    navigator.pop()
                                                    if (depth?.let { it <= 1 } == true) {
                                                        model.clear()
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (navigator.size <= 1) {
                                                    Icons.Rounded.Close
                                                } else {
                                                    Icons.Rounded.ChevronLeft
                                                },
                                                contentDescription = null
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    AnimatedVisibility(
                                        visible = !isNetworkConnected
                                    ) {
                                        PlainTooltipBox(
                                            tooltip = {
                                                Text(stringResource(MR.strings.status_network_unavailable))
                                            }
                                        ) {
                                            IconButton(
                                                onClick = {},
                                                enabled = false
                                            ) {
                                                Icon(Icons.Rounded.CloudOff, null)
                                            }
                                        }
                                    }
                                    IconButton(
                                        onClick = { searchModel.isSearching.value = true },
                                        enabled = areas.isNotEmpty()
                                    ) {
                                        Icon(Icons.Rounded.Search, null)
                                    }
                                }
                            )
                        }
                    }
                }
            ) { page ->
                when (page) {
                    0 -> ScreenTransition(
                        navigator,
                        transition = {
                            val initialDepth = (initialState as? DepthScreen)?.depth ?: 0
                            val targetDepth = (targetState as? DepthScreen)?.depth ?: 0
                            if (initialDepth < targetDepth) {
                                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                            } else {
                                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                            }
                        }
                    )

                    else -> Text("This is page $page")
                }
            }
        }
    }

    @Composable
    fun SearchBarLogic(
        areas: List<Area>,
        zones: List<Zone>,
        sectors: List<Sector>,
        paths: List<Path>,
        searchModel: SearchModel,
        navigator: Navigator
    ) {
        val searchQuery by searchModel.query.collectAsState("")
        val isSearching by searchModel.isSearching.collectAsState(false)

        val filterAreas = searchModel.filterAreas
        val filterZones = searchModel.filterZones
        val filterSectors = searchModel.filterSectors
        val filterPaths = searchModel.filterPaths

        fun <Type : Any> filter(
            list: List<Type>,
            filteredList: SnapshotStateList<Type?>,
            name: (Type) -> String,
            filters: List<Filter<Any>>
        ) {
            for ((index, item) in list.withIndex()) {
                val passesFilters = filters.all { it.show(item) }
                val passesQuery = name(item).unaccent().contains(searchQuery.unaccent(), true)
                val element = item.takeIf { passesFilters && passesQuery }
                if (filteredList.size > index) {
                    filteredList[index] = element
                } else {
                    filteredList.add(element)
                }
            }
        }

        val filteredAreas = searchModel.filteredAreas
        LaunchedEffect(areas, filterAreas, searchQuery) {
            filter(areas, filteredAreas, Area::displayName, filterAreas)
        }
        val filteredZones = searchModel.filteredZones
        LaunchedEffect(zones, filterZones, searchQuery) {
            filter(zones, filteredZones, Zone::displayName, filterZones)
        }
        val filteredSectors = searchModel.filteredSectors
        LaunchedEffect(sectors, filterSectors, searchQuery) {
            filter(sectors, filteredSectors, Sector::displayName, filterSectors)
        }
        val filteredPaths = searchModel.filteredPaths
        LaunchedEffect(paths, filterPaths, searchQuery) {
            filter(paths, filteredPaths, Path::displayName, filterPaths)
        }

        var showingFiltersDialog by remember { mutableStateOf(false) }
        if (showingFiltersDialog) {
            SearchFiltersDialog(
                filterAreas,
                filterZones,
                filterSectors,
                filterPaths,
                onDismissRequest = { showingFiltersDialog = false }
            )
        }

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchModel.query.value = it },
            onSearch = {},
            active = isSearching,
            onActiveChange = { searchModel.isSearching.value = it },
            placeholder = {
                Text(stringResource(MR.strings.search))
            },
            leadingIcon = {
                Icon(Icons.Outlined.Search, null)
            },
            trailingIcon = {
                Row {
                    IconButton(
                        onClick = { searchModel.query.value = "" }
                    ) {
                        Icon(Icons.Rounded.Close, null)
                    }
                    IconButton(
                        onClick = { showingFiltersDialog = true }
                    ) {
                        Icon(Icons.Rounded.FilterAlt, null)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (searchQuery.isBlank()) {
                Text(stringResource(MR.strings.search_empty))
            } else {
                LazyColumn {
                    items(
                        items = filteredAreas
                    ) { area ->
                        if (area == null) return@items
                        ListItem(
                            headlineContent = { Text(area.displayName) },
                            supportingContent = { Text("Area") },
                            modifier = Modifier.clickable {
                                navigator.push(
                                    ZonesScreen(area.id)
                                )
                                searchModel.dismiss()
                            }
                        )
                    }
                    items(
                        items = filteredZones
                    ) { zone ->
                        if (zone == null) return@items
                        ListItem(
                            headlineContent = { Text(zone.displayName) },
                            supportingContent = { Text("Zone") },
                            modifier = Modifier.clickable {
                                navigator.push(
                                    SectorsScreen(zone.id)
                                )
                                searchModel.dismiss()
                            }
                        )
                    }
                    items(
                        items = filteredSectors
                    ) { sector ->
                        if (sector == null) return@items
                        ListItem(
                            headlineContent = { Text(sector.displayName) },
                            supportingContent = { Text("Sector") },
                            modifier = Modifier.clickable {
                                navigator.push(
                                    PathsScreen(sector.id)
                                )
                                searchModel.dismiss()
                            }
                        )
                    }
                    items(
                        items = filteredPaths
                    ) { path ->
                        if (path == null) return@items
                        ListItem(
                            headlineContent = { Text(path.displayName) },
                            supportingContent = { Text("Path") },
                            modifier = Modifier.clickable {
                                navigator.push(
                                    PathsScreen(path.parentSectorId, highlightPathId = path.id)
                                )
                                searchModel.dismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}