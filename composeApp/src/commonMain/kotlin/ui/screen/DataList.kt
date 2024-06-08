package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import data.DataTypeWithImage
import data.Zone
import ui.list.DataCard
import ui.platform.MapComposable
import ui.reusable.CircularProgressIndicatorBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <Parent : DataTypeWithImage, ChildrenType : DataTypeWithImage> DataList(
    parent: Parent?,
    children: List<ChildrenType>?,
    onNavigationRequested: (ChildrenType) -> Unit,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { parent?.let { Text(it.displayName) } },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = parent,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { data ->
            if (data == null) {
                CircularProgressIndicatorBox()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (data is Zone) {
                        item {
                            MapComposable(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 8.dp)
                                    .widthIn(max = 600.dp)
                                    .height(180.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .shadow(3.dp),
                                kmzUUID = data.kmzUUID
                            )
                        }
                    }
                    if (children != null) {
                        items(children) { child ->
                            DataCard(
                                item = child,
                                imageHeight = 200.dp,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .padding(bottom = 12.dp)
                                    .widthIn(max = 600.dp)
                                    .fillMaxWidth()
                            ) { onNavigationRequested(child) }
                        }
                    }
                }
            }
        }
    }
}
