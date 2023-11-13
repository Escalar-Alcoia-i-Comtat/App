package ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cache.ImageCache
import data.model.DataTypeWithImage
import org.jetbrains.skia.Image

@Composable
fun <T: DataTypeWithImage> DataCard(
    item: T,
    imageHeight: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable(
            onClick = onClick
        )
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

        val image by ImageCache.collectStateOf(item.image)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight),
            contentAlignment = Alignment.Center
        ) {
            image?.let { data ->
                Image(
                    Image.makeFromEncoded(data).toComposeImageBitmap(),
                    contentDescription = item.displayName,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
