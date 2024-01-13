package ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import cache.ImageCache
import com.mxalbert.zoomable.Zoomable
import data.Path
import data.Sector
import kotlinx.coroutines.launch
import ui.list.PathListItem
import ui.model.PathsScreenModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
class PathsScreen(
    id: Long,
    private val highlightPathId: Long? = null
) : DataScreen<Sector, Path>(
    id = id,
    depth = @Suppress("MagicNumber") 3,
    { PathsScreenModel(it) },
    null
) {
    override fun shouldDisplaySidePanel(windowSizeClass: WindowSizeClass): Boolean {
        return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    }

    @Composable
    override fun ContentView(parentState: Sector, childrenState: List<Path>?) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Zoomable(
                modifier = Modifier.fillMaxWidth().weight(1f).clipToBounds()
            ) {
                val image by ImageCache.collectStateOf(parentState.image)

                image?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = parentState.displayName,
                        modifier = Modifier
                            .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                            .fillMaxSize()
                    )
                } ?: CircularProgressIndicator()
            }

            val windowSizeClass = calculateWindowSizeClass()
            val shouldDisplaySidePanel = remember(windowSizeClass) {
                shouldDisplaySidePanel(windowSizeClass)
            }
            AnimatedVisibility(
                visible = !shouldDisplaySidePanel
            ) {
                PathsListView(
                    childrenState,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
        }
    }

    @Composable
    override fun RowScope.SidePanel(parentState: Sector, childrenState: List<Path>?) {
        PathsListView(
            childrenState,
            modifier = Modifier.fillMaxHeight().weight(1f)
        )
    }

    @Composable
    fun PathsListView(childrenState: List<Path>?, modifier: Modifier = Modifier) {
        val scope = rememberCoroutineScope()
        val listState = rememberLazyListState()

        LaunchedEffect(highlightPathId, childrenState) {
            if (highlightPathId == null) return@LaunchedEffect
            if (childrenState == null) return@LaunchedEffect
            val index = childrenState.indexOfFirst { it.id == highlightPathId }
            if (index < 0) return@LaunchedEffect
            scope.launch { listState.animateScrollToItem(index) }
        }

        LazyColumn(
            modifier = modifier,
            state = listState
        ) {
            items(childrenState ?: emptyList()) { path ->
                PathListItem(
                    path,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    highlight = highlightPathId == path.id
                ) { /*TODO*/ }
            }
        }
    }
}
