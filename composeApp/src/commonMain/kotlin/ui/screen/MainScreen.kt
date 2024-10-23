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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.Area
import database.SettingsKeys
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.BackHandler
import sync.DataSync
import sync.SyncProcess
import ui.composition.LocalLifecycleManager
import ui.composition.LocalNavController
import ui.list.DataCard
import ui.navigation.Routes
import utils.IO

@Composable
fun MainScreen(
    areas: List<Area>?,
    syncStatus: SyncProcess.Status?
) {
    val lifecycleManager = LocalLifecycleManager.current

    var showConnectionNotAvailableWarning by remember { mutableStateOf(false) }

    // TODO: when connection is available, and connectionNotAvailableWarning is true, run sync
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                DataSync.start()
            } catch (_: IllegalStateException) {
                // There's no Internet connection, check if the data is downloaded
                val lastSync = settings.getLongOrNull(SettingsKeys.LAST_SYNC)
                if (lastSync != null) {
                    // At least one sync has occurred, we can safely ignore, and the data will
                    // be updated when a connection is available
                    Napier.i { "No connection is available. Won't synchronize." }
                } else {
                    showConnectionNotAvailableWarning = true
                }
            }
        }
    }

    BackHandler {
        lifecycleManager.finish()
    }

    AnimatedVisibility(
        visible = areas.isNullOrEmpty() && (syncStatus is SyncProcess.Status.RUNNING || syncStatus == SyncProcess.Status.WAITING),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    AreasList(areas, showConnectionNotAvailableWarning, syncStatus)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AreasList(
    areas: List<Area>?,
    connectionNotAvailableWarning: Boolean,
    status: SyncProcess.Status?
) {
    val navigator = LocalNavController.current

    LazyColumn(
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

        if (connectionNotAvailableWarning) item {
            // TODO: improve this
            Text("Network not available!")
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
            ) { navigator?.navigate(Routes.area(area.id)) }
        }

        // Add some padding at the end
        item { Spacer(Modifier.height(8.dp)) }
    }
}
