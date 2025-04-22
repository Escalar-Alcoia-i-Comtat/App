package ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import data.DataTypeWithImage
import data.DataTypeWithPoint
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import platform.launchPoint
import ui.reusable.ContextMenu

@Composable
fun <T : DataTypeWithImage> DataCard(
    item: T,
    imageHeight: Dp,
    modifier: Modifier = Modifier,
    prefixContent: (@Composable RowScope.() -> Unit)? = null,
    onEdit: (() -> Unit)?,
    onClick: () -> Unit
) {
    ContextMenu(
        enabled = onEdit != null,
        dropdownContent = {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.editor_edit)) },
                onClick = onEdit ?: {}
            )
        }
    ) {
        Column(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                prefixContent?.invoke(this)
                Text(
                    text = item.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    fontSize = 20.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                val painter = rememberAsyncImagePainter(item.imageUrl())
                val state by painter.state.collectAsState()
                when (state) {
                    is AsyncImagePainter.State.Empty,
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AsyncImagePainter.State.Success -> {
                        Image(
                            painter = painter,
                            contentDescription = item.displayName,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    is AsyncImagePainter.State.Error -> {
                        // Show some error UI.
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    onEdit?.let {
                        SmallFloatingActionButton(
                            onClick = it,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(Icons.Outlined.Edit, null)
                        }
                    }
                    if (item is DataTypeWithPoint) item.point?.let { point ->
                        SmallFloatingActionButton(
                            onClick = { launchPoint(point, item.displayName) }
                        ) {
                            Icon(Icons.Outlined.Place, null)
                        }
                    }
                }
            }
        }
    }
}
