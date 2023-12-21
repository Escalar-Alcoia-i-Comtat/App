package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import cache.ImageCache
import cafe.adriel.voyager.core.screen.Screen
import data.Area
import database.SettingsKeys
import database.database
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import sync.DataSync
import sync.SyncProcess
import ui.list.DataCard

object MainScreen: Screen {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val status by DataSync.status

        val areas by database.areaQueries
            .getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .collectAsState(emptyList())

        /**
         * If true, a warning will be shown notifying the user that a network connection is not
         * available, and that no data is downloaded locally, so it's needed to connect at some
         * point to fetch the data.
         */
        var connectionNotAvailableWarning by remember { mutableStateOf(false) }

        // TODO: when connection is available, and connectionNotAvailableWarning is true, run sync
        LaunchedEffect(Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    DataSync.start()

                    ImageCache.updateCache()
                } catch (_: IllegalStateException) {
                    // There's no Internet connection, check if the data is downloaded
                    val lastSync = settings.getLongOrNull(SettingsKeys.LAST_SYNC)
                    if (lastSync != null) {
                        // At least one sync has occurred, we can safely ignore, and the data will
                        // be updated when a connection is available
                        Napier.i { "No connection is available. Won't synchronize." }
                    } else {
                        connectionNotAvailableWarning = true
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = areas.isEmpty() && (status is SyncProcess.Status.RUNNING || status == SyncProcess.Status.WAITING),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            stickyHeader {
                AnimatedContent(status) { currentStatus ->
                    if (areas.isNotEmpty() && currentStatus is SyncProcess.Status.RUNNING) {
                        LinearProgressIndicator(
                            progress = currentStatus.progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (areas.isNotEmpty() && currentStatus == SyncProcess.Status.WAITING) {
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
                items = areas.sortedBy { it.displayName }
            ) { area ->
                DataCard(
                    item = Area(area),
                    imageHeight = 200.dp,
                    modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 12.dp)
                ) {}
            }

            // Add some padding at the end
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
