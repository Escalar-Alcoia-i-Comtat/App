package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cache.ImageCache
import com.mxalbert.zoomable.Zoomable
import data.Path
import data.Sector
import kotlinx.coroutines.launch
import ui.list.PathListItem
import ui.model.PathsScreenModel

@OptIn(ExperimentalMaterial3Api::class)
class PathsScreen(
    id: Long,
    private val highlightPathId: Long? = null
) : DataScreen<Sector, Path>(
    id = id,
    depth = @Suppress("MagicNumber") 3,
    { PathsScreenModel(it) },
    null
) {
    @Composable
    override fun ContentView(parentState: Sector, childrenState: List<Path>?) {
        Column {
            val scope = rememberCoroutineScope()
            val listState = rememberLazyListState()

            Zoomable(
                modifier = Modifier.fillMaxWidth().weight(1f)
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

            LaunchedEffect(highlightPathId, childrenState) {
                if (highlightPathId == null) return@LaunchedEffect
                if (childrenState == null) return@LaunchedEffect
                val index = childrenState.indexOfFirst { it.id == highlightPathId }
                if (index < 0) return@LaunchedEffect
                scope.launch { listState.animateScrollToItem(index) }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
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
}
