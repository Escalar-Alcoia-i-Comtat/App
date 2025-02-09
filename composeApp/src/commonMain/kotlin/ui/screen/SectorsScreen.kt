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
    onEditRequested: ((sector: Sector) -> Unit)?,
    viewModel: SectorsScreenModel = viewModel { SectorsScreenModel() },
    scrollToId: Long? = null
) {
    val zone by viewModel.parent.collectAsState()
    val sectors by viewModel.children.collectAsState()

    LaunchedEffect(zoneId) {
        viewModel.load(zoneId, onBackRequested)
    }

    BackHandler(onBack = onBackRequested)

    DataList(
        parent = zone,
        children = sectors,
        scrollToId = scrollToId,
        onNavigationRequested = { onSectorRequested(it.id) },
        onEditRequested = onEditRequested,
        onNavigateUp = onBackRequested
    )
}
