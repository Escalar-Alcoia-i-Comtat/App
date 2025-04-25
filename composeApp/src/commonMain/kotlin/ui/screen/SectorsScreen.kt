package ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import data.Sector
import platform.BackHandler
import ui.model.SectorsScreenModel

@Composable
fun SectorsScreen(
    zoneId: Long,
    onBackRequested: () -> Unit,
    onSectorRequested: (sectorId: Long) -> Unit,
    onEditZoneRequested: (() -> Unit)?,
    onEditSectorRequested: ((sector: Sector) -> Unit)?,
    onCreateSectorRequested: (() -> Unit)?,
    viewModel: SectorsScreenModel = viewModel { SectorsScreenModel() },
    scrollToId: Long? = null
) {
    val zone by viewModel.parent.collectAsState()
    val sectors by viewModel.children.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(zoneId) {
        viewModel.load(zoneId, onBackRequested)
    }

    BackHandler(onBack = onBackRequested)

    DataList(
        parent = zone,
        children = sectors,
        scrollToId = scrollToId,
        onNavigationRequested = { onSectorRequested(it.id) },
        onEditRequested = onEditZoneRequested,
        onEditChildRequested = onEditSectorRequested,
        onCreateRequested = onCreateSectorRequested,
        isLoading = isLoading,
        onItemMoved = viewModel::moveItem,
        onFinishSorting = viewModel::saveMovedItems,
        onNavigateUp = onBackRequested
    )
}
