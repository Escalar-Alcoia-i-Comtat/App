import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import database.collectAsStateList
import database.database
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import network.connectivityStatus
import resources.MR
import sync.DataSync
import sync.SyncProcess
import ui.navigation.AdaptiveNavigationScaffold
import ui.navigation.NavigationItem

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun App() {
    DisposableEffect(Unit) {
        connectivityStatus.start()

        onDispose {
            connectivityStatus.stop()
        }
    }

    MaterialTheme {
        val status by DataSync.status

        val isNetworkConnected by connectivityStatus.isNetworkConnected.collectAsState()

        val areas by database.areaQueries
            .getAll()
            .collectAsStateList()

        LaunchedEffect(Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                DataSync.start()
            }
        }

        AdaptiveNavigationScaffold(
            items = listOf(
                NavigationItem(
                    label = { stringResource(MR.strings.navigation_explore) },
                    icon = { Icons.Outlined.Explore }
                ),
                NavigationItem(
                    label = { stringResource(MR.strings.navigation_settings) },
                    icon = { Icons.Outlined.Settings }
                )
            ),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Escalar Alcoià i Comtat") },
                    actions = {
                        AnimatedVisibility(
                            visible = !isNetworkConnected
                        ) {
                            PlainTooltipBox(
                                tooltip = { Text("Network not available") }
                            ) {
                                IconButton(
                                    onClick = {}
                                ) {
                                    Icon(Icons.Rounded.CloudOff, null)
                                }
                            }
                        }
                    }
                )
            }
        ) { page ->
            when (page) {
                0 -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    stickyHeader {
                        AnimatedContent(status) { currentStatus ->
                            if (currentStatus is SyncProcess.Status.RUNNING) {
                                LinearProgressIndicator(
                                    progress = currentStatus.progress,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else if (currentStatus == SyncProcess.Status.WAITING) {
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    if (status == SyncProcess.Status.FINISHED) {
                        items(areas) { area ->
                            Text(area.displayName)
                        }
                    }
                }
                else -> Text("This is page $page")
            }
        }
    }
}