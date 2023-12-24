package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cache.ImageCache
import com.mxalbert.zoomable.Zoomable
import data.Path
import data.Sector
import ui.model.PathsScreenModel

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
