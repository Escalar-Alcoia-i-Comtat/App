package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.Area
import platform.BackHandler
import sync.SyncProcess
import ui.composition.LocalLifecycleManager
import ui.list.DataCard

@Composable
fun MainScreen(
    areas: List<Area>,
    syncStatus: SyncProcess.Status?,
    onAreaRequested: (areaId: Long) -> Unit,
    scrollToId: Long? = null
) {
    val lifecycleManager = LocalLifecycleManager.current

    BackHandler {
        lifecycleManager.finish()
    }

    AnimatedVisibility(
        visible = areas.isEmpty() && (syncStatus is SyncProcess.Status.RUNNING || syncStatus == SyncProcess.Status.WAITING),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    AreasList(areas, syncStatus, onAreaRequested, scrollToId)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AreasList(
    areas: List<Area>?,
    status: SyncProcess.Status?,
    onAreaRequested: (areaId: Long) -> Unit,
    scrollToId: Long? = null
) {
    val state = rememberLazyListState()

    LaunchedEffect(scrollToId) {
        scrollToId ?: return@LaunchedEffect

        val index = areas?.indexOfFirst { it.id == scrollToId } ?: return@LaunchedEffect
        state.animateScrollToItem(index)
    }

    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader {
            AnimatedContent(status) { currentStatus ->
                if (!areas.isNullOrEmpty() && currentStatus is SyncProcess.Status.RUNNING) {
                    LinearProgressIndicator(
                        progress = { currentStatus.progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (!areas.isNullOrEmpty() && currentStatus == SyncProcess.Status.WAITING) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        items(
            contentType = { "area" },
            key = { it.id },
            items = areas?.sortedBy { it.displayName } ?: emptyList()
        ) { area ->
            DataCard(
                item = area,
                imageHeight = 200.dp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 12.dp)
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) { onAreaRequested(area.id) }
        }

        // Add some padding at the end
        item { Spacer(Modifier.height(8.dp)) }
    }
}
