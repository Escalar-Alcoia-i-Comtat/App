package ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cache.ImageCache
import data.Path
import data.Sector
import ui.model.PathsScreenModel
import ui.platform.ZoomableImage

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
            val image by ImageCache.collectStateOf(parentState.image)
            ZoomableImage(
                image = image,
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentDescription = parentState.displayName
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(childrenState ?: emptyList()) { path ->
                    ListItem(
                        headlineContent = { Text(path.displayName) }
                    )
                }
            }
        }
    }
}
