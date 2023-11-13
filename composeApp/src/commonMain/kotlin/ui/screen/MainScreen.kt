package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import data.Area
import database.collectAsStateList
import database.database
import sync.DataSync
import sync.SyncProcess
import ui.list.DataCard

object MainScreen: Screen {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val status by DataSync.status

        val areas by database.areaQueries
            .getAll()
            .collectAsStateList()

        LazyColumn(
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
                    DataCard(Area(area), 200.dp) {}
                }
            }
        }
    }
}
