package org.escalaralcoiaicomtat.app.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.sync.SyncProcess
import org.escalaralcoiaicomtat.app.ui.list.DataCard
import org.escalaralcoiaicomtat.app.ui.modifier.sharedElement

@Composable
fun MainScreen(
    areas: List<Area>?,
    syncStatus: SyncProcess.Status?,
    onAreaRequested: (areaId: Long) -> Unit,
    onEditRequested: ((area: Area) -> Unit)?,
    scrollToId: Long? = null
) {
    AnimatedContent(
        targetState = areas to syncStatus,
        modifier = Modifier.fillMaxSize()
    ) { (list, status) ->
        if (list == null || (status is SyncProcess.Status.RUNNING || status == SyncProcess.Status.WAITING)) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (status is SyncProcess.Status.RUNNING && status.hasProgress()) {
                    CircularProgressIndicator({ status.progress })
                } else {
                    CircularProgressIndicator()
                }
            }
        } else {
            AreasList(list, onAreaRequested, onEditRequested, scrollToId)
        }
    }
}

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
private fun AreasList(
    areas: List<Area>,
    onAreaRequested: (areaId: Long) -> Unit,
    onEditRequested: ((area: Area) -> Unit)?,
    scrollToId: Long? = null
) {
    val state = rememberLazyListState()

    LaunchedEffect(scrollToId) {
        scrollToId ?: return@LaunchedEffect

        val index =
            areas.indexOfFirst { it.id == scrollToId }.takeIf { it >= 0 } ?: return@LaunchedEffect
        state.animateScrollToItem(index)
    }

    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            contentType = { "area" },
            key = { it.id },
            items = areas.sortedBy { it.displayName }
        ) { area ->
            DataCard(
                item = area,
                imageHeight = 200.dp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 12.dp)
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .sharedElement("area-${area.id}"),
                onEdit = onEditRequested?.let { { it(area) } }
            ) { onAreaRequested(area.id) }
        }

        // Add some padding at the end
        item { Spacer(Modifier.height(8.dp)) }
    }
}
