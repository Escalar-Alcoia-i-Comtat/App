package org.escalaralcoiaicomtat.app.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.ui.model.SectorsScreenModel
import kotlin.uuid.Uuid

@Composable
fun SectorsScreen(
    zoneId: Long,
    editAllowed: Boolean,
    onBackRequested: () -> Unit,
    onSectorRequested: (sectorId: Long) -> Unit,
    onEditZoneRequested: (() -> Unit)?,
    onEditSectorRequested: ((sector: Sector) -> Unit)?,
    onCreateSectorRequested: (() -> Unit)?,
    onMapClicked: (Uuid?) -> Unit,
    viewModel: SectorsScreenModel = viewModel { SectorsScreenModel() },
    scrollToId: Long? = null
) {
    val zone by viewModel.parent.collectAsState()
    val sectors by viewModel.children.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(zoneId) {
        viewModel.load(zoneId, onBackRequested)
    }

    DataList(
        parent = zone,
        children = sectors,
        scrollToId = scrollToId,
        onNavigationRequested = { onSectorRequested(it.id) },
        onEditRequested = onEditZoneRequested,
        onEditChildRequested = onEditSectorRequested,
        onCreateRequested = onCreateSectorRequested,
        isLoading = isLoading,
        onItemMoved = viewModel::moveItem.takeIf { editAllowed },
        onFinishSorting = viewModel::saveMovedItems,
        onNavigateUp = onBackRequested,
        parentAnimationKey = { "zone-${zoneId}" },
        childAnimationKey = { "sector-${it.id}" },
        onMapClicked = onMapClicked,
    )
}
