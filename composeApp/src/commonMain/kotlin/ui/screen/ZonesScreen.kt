package ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import ui.model.ZonesScreenModel

@Composable
fun ZonesScreen(
    areaId: Long,
    onBackRequested: () -> Unit,
    onZoneRequested: (zoneId: Long) -> Unit,
    viewModel: ZonesScreenModel = viewModel { ZonesScreenModel() },
    scrollToId: Long? = null
) {
    val area by viewModel.parent.collectAsState()
    val zones by viewModel.children.collectAsState()

    LaunchedEffect(areaId) {
        viewModel.load(areaId, onBackRequested)
    }

    DataList(
        parent = area,
        children = zones,
        scrollToId = scrollToId,
        onNavigationRequested = { onZoneRequested(it.id) },
        onNavigateUp = onBackRequested,
    )
}
