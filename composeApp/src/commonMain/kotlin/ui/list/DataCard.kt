package ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cache.ImageCache
import data.DataTypeWithImage
import data.DataTypeWithPoint
import data.DataTypeWithPoints
import platform.launchPoint

@Composable
fun <T: DataTypeWithImage> DataCard(
    item: T,
    imageHeight: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = item.displayName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            fontSize = 20.sp
        )

        var progress by remember { mutableStateOf<Float?>(null) }
        val image by ImageCache.collectStateOf(item.image) { current, max ->
            max ?: return@collectStateOf
            progress = if (current == max) {
                null
            } else {
                (current.toDouble() / max).toFloat()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            image?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = item.displayName,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            } ?: progress?.let { CircularProgressIndicator({ it }) } ?: CircularProgressIndicator()

            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                if (item is DataTypeWithPoints) {
                    for (point in item.points) {
                        SmallFloatingActionButton(
                            onClick = { launchPoint(point.location, item.displayName) },
                            modifier = Modifier.padding(bottom = 4.dp).size(32.dp)
                        ) {
                            Icon(point.iconVector, null)
                        }
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
