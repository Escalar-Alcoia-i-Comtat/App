import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import database.collectAsStateList
import database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import network.connectivityStatus
import sync.DataSync
import sync.SyncProcess

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

        Scaffold(
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
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
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
        }
    }
}