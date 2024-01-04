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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.coroutines.mapToList
import cache.ImageCache
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import data.Area
import database.SettingsKeys
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import sync.DataSync
import sync.SyncProcess
import ui.list.DataCard
import ui.model.MainScreenModel

@OptIn(ExperimentalFoundationApi::class)
object MainScreen: Screen {
    @Composable
    override fun Content() {
        val status by DataSync.status

        val screenModel = rememberScreenModel { MainScreenModel() }

        val areas by screenModel.areas
            .mapToList(Dispatchers.Default)
            .collectAsState(emptyList())

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
                        screenModel.showConnectionNotAvailableWarning.emit(true)
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

        AreasList(screenModel, status)
    }

    @Composable
    fun AreasList(
        model: MainScreenModel,
        status: SyncProcess.Status
    ) {
        val navigator = LocalNavigator.current

        val connectionNotAvailableWarning by model.showConnectionNotAvailableWarning.collectAsState(false)

        val areas by model.areas
            .mapToList(Dispatchers.Default)
            .collectAsState(emptyList())

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
                ) { navigator?.push(ZonesScreen(area.id)) }
            }

            // Add some padding at the end
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
