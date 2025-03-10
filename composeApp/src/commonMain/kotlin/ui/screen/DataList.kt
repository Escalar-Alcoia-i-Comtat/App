package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import data.DataTypeWithImage
import data.Zone
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import platform.launchPoint
import ui.list.DataCard
import ui.list.LocationCard
import ui.platform.MapComposable
import ui.reusable.CircularProgressIndicatorBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <Parent : DataTypeWithImage, ChildrenType : DataTypeWithImage> DataList(
    parent: Parent?,
    children: List<ChildrenType>?,
    scrollToId: Long? = null,
    onNavigationRequested: (ChildrenType) -> Unit,
    onEditRequested: (() -> Unit)?,
    onEditChildRequested: ((ChildrenType) -> Unit)?,
    onCreateRequested: (() -> Unit)?,
    onNavigateUp: () -> Unit
) {
    val state = rememberLazyListState()

    LaunchedEffect(scrollToId) {
        scrollToId ?: return@LaunchedEffect

        val index = children?.indexOfFirst { it.id == scrollToId } ?: return@LaunchedEffect
        state.animateScrollToItem(index)
    }

    // TODO: Allow sorting elements to admins

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { parent?.let { Text(it.displayName) } },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                actions = {
                    if (onEditRequested != null) {
                        IconButton(onClick = onEditRequested) {
                            Icon(Icons.Default.Edit, stringResource(Res.string.editor_edit))
                        }
                    }
                    if (onCreateRequested != null) {
                        IconButton(onClick = onCreateRequested) {
                            Icon(Icons.Default.Add, stringResource(Res.string.editor_create))
                        }
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
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (data is Zone) display(data)

                    if (children != null) {
                        items(
                            items = children,
                            key = { it.id },
                            contentType = { it::class.simpleName }
                        ) { child ->
                            DataCard(
                                item = child,
                                imageHeight = 200.dp,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .padding(bottom = 12.dp)
                                    .widthIn(max = 600.dp)
                                    .fillMaxWidth(),
                                onEdit = onEditChildRequested?.let { { it(child) } }
                            ) { onNavigationRequested(child) }
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.display(zone: Zone) {
    item(key = "zone-map-${zone.id}", contentType = "zone-map") {
        MapComposable(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
                .widthIn(max = 600.dp)
                .height(180.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .shadow(3.dp),
            kmz = zone.kmz
        )
    }
    if (zone.hasAnyMetadata()) {
        item(key = "title", contentType = "zone-metadata") {
            Text(
                text = stringResource(Res.string.zone_information_title),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            )
        }
        if (zone.point != null) {
            item(key = "point", contentType = "zone-metadata") {
                LocationCard(
                    icon = Icons.Default.OutlinedFlag,
                    title = stringResource(Res.string.zone_information_location),
                    point = zone.point,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                ) { launchPoint(zone.point, zone.displayName) }
            }
        }
        items(
            items = zone.points,
            key = { "point-${it.hashCode()}" },
            contentType = { "zone-metadata" }
        ) { point ->
            LocationCard(
                icon = point.icon.iconVector,
                title = point.displayName(),
                point = point.location,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) { launchPoint(point.location, point.label) }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}
