package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.lifecycle.viewmodel.compose.viewModel
import data.Area
import data.Path
import data.Sector
import data.Zone
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.aakira.napier.Napier
import network.connectivityStatus
import org.jetbrains.compose.resources.stringResource
import platform.BackHandler
import search.Filter
import sync.SyncProcess
import ui.composition.LocalLifecycleManager
import ui.dialog.SearchFiltersDialog
import ui.model.AppScreenModel
import ui.model.SearchModel
import ui.navigation.AdaptiveNavigationScaffold
import ui.navigation.NavigationItem
import ui.pages.SettingsPage
import ui.state.LaunchedKeyEvent
import utils.unaccent

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun AppScreen(
    onAreaRequested: (areaId: Long) -> Unit,
    onZoneRequested: (parentAreaId: Long, zoneId: Long) -> Unit,
    onSectorRequested: (parentAreaId: Long, parentZoneId: Long, sectorId: Long, pathId: Long?) -> Unit,
    appScreenModel: AppScreenModel = viewModel { AppScreenModel() },
    searchModel: SearchModel = viewModel<SearchModel> { SearchModel() },
    scrollToId: Long? = null
) {
    val isNetworkConnected by connectivityStatus.isNetworkConnected.collectAsState()

    val areas by appScreenModel.areas.collectAsState()
    val zones by appScreenModel.zones.collectAsState()
    val sectors by appScreenModel.sectors.collectAsState()
    val paths by appScreenModel.paths.collectAsState()

    val syncStatus by appScreenModel.syncStatus.collectAsState(SyncProcess.Status.WAITING)

    LaunchedEffect(areas) {
        Napier.i { "There are ${areas.size} areas loaded" }
    }

    LaunchedKeyEvent { event ->
        if (event.isCtrlPressed && event.key == Key.F && event.type == KeyEventType.KeyUp) {
            searchModel.search()
            true
        } else if (event.key == Key.Escape && event.type == KeyEventType.KeyUp) {
            searchModel.dismiss()
            true
        } else {
            false
        }
    }

    val lifecycleManager = LocalLifecycleManager.current
    BackHandler { lifecycleManager.finish() }

    AdaptiveNavigationScaffold(
        items = listOf(
            NavigationItem(
                label = { stringResource(Res.string.navigation_explore) },
                icon = { Icons.Outlined.Explore }
            ),
            NavigationItem(
                label = { stringResource(Res.string.navigation_settings) },
                icon = { Icons.Outlined.Settings }
            )
        ),
        topBar = {
            val searchQuery by searchModel.query.collectAsState("")
            val isSearching by searchModel.isSearching.collectAsState(false)

            AnimatedContent(
                targetState = isSearching
            ) { searching ->
                if (searching) {
                    SearchBarLogic(
                        areas,
                        zones,
                        sectors,
                        paths,
                        searchQuery,
                        isSearching,
                        searchModel.filterAreas,
                        searchModel.filteredAreas,
                        searchModel.filterZones,
                        searchModel.filteredZones,
                        searchModel.filterSectors,
                        searchModel.filteredSectors,
                        searchModel.filterPaths,
                        searchModel.filteredPaths,
                        searchModel::search,
                        searchModel::dismiss,
                        searchModel::search,
                        onAreaRequested,
                        onZoneRequested,
                        onSectorRequested
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = { Text("Escalar Alcoià i Comtat") },
                        actions = {
                            AnimatedVisibility(
                                visible = !isNetworkConnected
                            ) {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    state = rememberTooltipState(),
                                    tooltip = {
                                        PlainTooltip {
                                            Text(stringResource(Res.string.status_network_unavailable))
                                        }
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
                                onClick = searchModel::search,
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
            0 -> MainScreen(areas, syncStatus, onAreaRequested, scrollToId)

            1 -> SettingsPage()

            else -> Text("This is page $page")
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
fun SearchBarLogic(
    areas: List<Area>?,
    zones: List<Zone>?,
    sectors: List<Sector>?,
    paths: List<Path>?,
    searchQuery: String,
    isSearching: Boolean,
    filterAreas: SnapshotStateList<Filter<Any>>,
    filteredAreas: SnapshotStateList<Area?>,
    filterZones: SnapshotStateList<Filter<Any>>,
    filteredZones: SnapshotStateList<Zone?>,
    filterSectors: SnapshotStateList<Filter<Any>>,
    filteredSectors: SnapshotStateList<Sector?>,
    filterPaths: SnapshotStateList<Filter<Any>>,
    filteredPaths: SnapshotStateList<Path?>,
    onSearchRequested: () -> Unit,
    onSearchDismissed: () -> Unit,
    onSearchQuery: (String) -> Unit,
    onAreaRequested: (areaId: Long) -> Unit,
    onZoneRequested: (parentAreaId: Long, zoneId: Long) -> Unit,
    onSectorRequested: (parentAreaId: Long, parentZoneId: Long, sectorId: Long, pathId: Long?) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    // Focus the search bar when it is shown
    LaunchedEffect(isSearching) { if (isSearching) focusRequester.requestFocus() }

    fun <Type : Any> filter(
        list: List<Type>?,
        filteredList: SnapshotStateList<Type?>,
        name: (Type) -> String,
        filters: List<Filter<Any>>
    ) {
        if (list == null) return
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

    LaunchedEffect(areas, filterAreas, searchQuery) {
        filter(areas, filteredAreas, Area::displayName, filterAreas)
    }
    LaunchedEffect(zones, filterZones, searchQuery) {
        filter(zones, filteredZones, Zone::displayName, filterZones)
    }
    LaunchedEffect(sectors, filterSectors, searchQuery) {
        filter(sectors, filteredSectors, Sector::displayName, filterSectors)
    }
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
        inputField = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQuery,
                placeholder = { Text(stringResource(Res.string.search)) },
                leadingIcon = { Icon(Icons.Outlined.Search, null) },
                trailingIcon = {
                    Row {
                        IconButton(
                            onClick = {
                                if (searchQuery.isBlank()) {
                                    onSearchDismissed()
                                } else {
                                    onSearchQuery("")
                                }
                            }
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
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )
        },
        expanded = isSearching,
        onExpandedChange = { if (it) onSearchRequested() else onSearchDismissed() },
    ) {
        if (searchQuery.isBlank()) {
            Text(stringResource(Res.string.search_empty))
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
                            onAreaRequested(area.id)
                            onSearchDismissed()
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
                            onZoneRequested(zone.parentAreaId, zone.id)
                            onSearchDismissed()
                        }
                    )
                }
                items(
                    items = filteredSectors
                ) { sector ->
                    if (sector == null) return@items
                    val zone = zones?.find { it.id == sector.parentZoneId } ?: return@items
                    ListItem(
                        headlineContent = { Text(sector.displayName) },
                        supportingContent = { Text("Sector") },
                        modifier = Modifier.clickable {
                            onSectorRequested(zone.parentAreaId, sector.parentZoneId, sector.id, null)
                            onSearchDismissed()
                        }
                    )
                }
                items(
                    items = filteredPaths
                ) { path ->
                    if (path == null) return@items
                    val sector = sectors?.find { it.id == path.parentSectorId } ?: return@items
                    val zone = zones?.find { it.id == sector.parentZoneId } ?: return@items
                    ListItem(
                        headlineContent = { Text(path.displayName) },
                        supportingContent = { Text("Path") },
                        modifier = Modifier.clickable {
                            onSectorRequested(zone.parentAreaId, sector.parentZoneId, path.parentSectorId, path.id)
                            onSearchDismissed()
                        }
                    )
                }
            }
        }
    }
}
