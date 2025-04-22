package ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import data.Zone
import platform.BackHandler
import ui.model.ZonesScreenModel

@Composable
fun ZonesScreen(
    areaId: Long,
    onBackRequested: () -> Unit,
    onZoneRequested: (zoneId: Long) -> Unit,
    onEditAreaRequested: (() -> Unit)?,
    onEditZoneRequested: ((zone: Zone) -> Unit)?,
    onCreateZoneRequested: (() -> Unit)?,
    viewModel: ZonesScreenModel = viewModel { ZonesScreenModel() },
    scrollToId: Long? = null
) {
    val area by viewModel.parent.collectAsState()
    val zones by viewModel.children.collectAsState()

    LaunchedEffect(areaId) {
        viewModel.load(areaId, onBackRequested)
    }

    BackHandler(onBack = onBackRequested)

    DataList(
        parent = area,
        children = zones,
        scrollToId = scrollToId,
        onNavigationRequested = { onZoneRequested(it.id) },
        onEditRequested = onEditAreaRequested,
        onEditChildRequested = onEditZoneRequested,
        onCreateRequested = onCreateZoneRequested,
        onItemDragged = null,
        onNavigateUp = onBackRequested,
    )
}
