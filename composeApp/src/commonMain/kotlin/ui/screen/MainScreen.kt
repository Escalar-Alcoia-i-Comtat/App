package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
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
                items(
                    contentType = { "area" },
                    key = { it.id },
                    items = areas.sortedBy { it.displayName }
                ) { area ->
                    DataCard(
                        item = Area(area),
                        imageHeight = 200.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {}
                }
            }

            // Add some padding at the end
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
