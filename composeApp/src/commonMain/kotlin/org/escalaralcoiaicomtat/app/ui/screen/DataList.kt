package org.escalaralcoiaicomtat.app.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.escalaralcoiaicomtat.app.data.DataTypeWithImage
import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.platform.launchPoint
import org.escalaralcoiaicomtat.app.ui.list.DataCard
import org.escalaralcoiaicomtat.app.ui.list.LocationCard
import org.escalaralcoiaicomtat.app.ui.modifier.sharedElement
import org.escalaralcoiaicomtat.app.ui.platform.MapComposable
import org.escalaralcoiaicomtat.app.ui.reusable.CircularProgressIndicatorBox
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun <Parent : DataTypeWithImage, ChildrenType : DataTypeWithImage> DataList(
    parent: Parent?,
    children: List<ChildrenType>?,
    scrollToId: Long? = null,
    onNavigationRequested: (ChildrenType) -> Unit,
    onEditRequested: (() -> Unit)?,
    onEditChildRequested: ((ChildrenType) -> Unit)?,
    onCreateRequested: (() -> Unit)?,
    isLoading: Boolean = false,
    onItemMoved: ((fromIndex: Int, toIndex: Int) -> Unit)? = null,
    onFinishSorting: (() -> Unit)? = null,
    onMapClicked: (kmz: Uuid?) -> Unit = {},
    parentAnimationKey: () -> String,
    childAnimationKey: (ChildrenType) -> String,
    onNavigateUp: () -> Unit,
) {
    val state = rememberLazyListState()
    var canSort by remember { mutableStateOf(false) }

    LaunchedEffect(scrollToId) {
        scrollToId ?: return@LaunchedEffect

        val index = children?.indexOfFirst { it.id == scrollToId } ?: return@LaunchedEffect
        state.animateScrollToItem(index)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    parent?.let {
                        Text(
                            text = it.displayName,
                            modifier = Modifier.sharedElement(key = parentAnimationKey()),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                actions = {
                    if (isLoading) {
                        CircularProgressIndicator()
                    }
                    if (onItemMoved != null) {
                        val infiniteTransition = rememberInfiniteTransition()
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = -10f,
                            targetValue = 10f,
                            animationSpec = infiniteRepeatable(tween(100), RepeatMode.Reverse)
                        )
                        IconButton(
                            modifier = Modifier.rotate(if (canSort) rotation else 0f),
                            onClick = {
                                canSort = !canSort
                                if (!canSort) onFinishSorting?.invoke()
                            },
                        ) {
                            Icon(Icons.Default.Reorder, stringResource(Res.string.editor_reorder))
                        }
                    }
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (data is Zone) display(data) { onMapClicked(data.kmz) }

                    if (children != null) {
                        itemsIndexed(
                            items = children,
                            key = { _, item -> item::class.simpleName + item.id },
                            contentType = { _, item -> item::class.simpleName },
                        ) { i, child ->
                            DataCard(
                                item = child,
                                imageHeight = 200.dp,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .padding(bottom = 22.dp)
                                    .widthIn(max = 600.dp)
                                    .fillMaxWidth(),
                                onEdit = onEditChildRequested?.let { { it(child) } },
                                animationKey = { childAnimationKey(child) },
                                prefixContent = {
                                    AnimatedVisibility(
                                        visible = canSort,
                                        enter = slideInHorizontally { -it },
                                        exit = slideOutHorizontally { -it },
                                    ) {
                                        Row {
                                            IconButton(
                                                onClick = { onItemMoved?.invoke(i, i - 1) },
                                                enabled = i > 0,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowUpward,
                                                    contentDescription = stringResource(Res.string.editor_reorder_up),
                                                )
                                            }
                                            IconButton(
                                                onClick = { onItemMoved?.invoke(i, i + 1) },
                                                enabled = i + 1 < children.size,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowDownward,
                                                    contentDescription = stringResource(Res.string.editor_reorder_down),
                                                )
                                            }
                                        }
                                    }
                                },
                            ) { onNavigationRequested(child) }
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.display(zone: Zone, onMapClicked: () -> Unit) {
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
            kmz = zone.kmz,
            onMapClick = onMapClicked,
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
                    description = null,
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
                description = point.description,
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
