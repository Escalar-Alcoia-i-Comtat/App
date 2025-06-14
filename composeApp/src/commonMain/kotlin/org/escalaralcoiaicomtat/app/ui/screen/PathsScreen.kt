package org.escalaralcoiaicomtat.app.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SwipeDownAlt
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.github.panpf.zoomimage.ZoomImage
import com.mikepenz.markdown.m3.Markdown
import com.russhwolf.settings.ExperimentalSettingsApi
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.aakira.napier.Napier
import io.ktor.http.URLParserException
import io.ktor.http.Url
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.generic.PitchInfo
import org.escalaralcoiaicomtat.app.data.generic.SafesCount
import org.escalaralcoiaicomtat.app.data.generic.SportsGrade
import org.escalaralcoiaicomtat.app.platform.launchPoint
import org.escalaralcoiaicomtat.app.platform.launchUrl
import org.escalaralcoiaicomtat.app.ui.composition.LocalUnitsConfiguration
import org.escalaralcoiaicomtat.app.ui.dialog.EditBlockingDialog
import org.escalaralcoiaicomtat.app.ui.icons.ClimbingAnchor
import org.escalaralcoiaicomtat.app.ui.icons.ClimbingHelmet
import org.escalaralcoiaicomtat.app.ui.icons.ClimbingShoes
import org.escalaralcoiaicomtat.app.ui.icons.Rope
import org.escalaralcoiaicomtat.app.ui.list.PathListItem
import org.escalaralcoiaicomtat.app.ui.model.PathsScreenModel
import org.escalaralcoiaicomtat.app.ui.modifier.sharedElement
import org.escalaralcoiaicomtat.app.ui.reusable.CircularProgressIndicatorBox
import org.escalaralcoiaicomtat.app.ui.reusable.ContextMenu
import org.escalaralcoiaicomtat.app.ui.reusable.ContextMenuItem
import org.escalaralcoiaicomtat.app.ui.reusable.ImageLoadError
import org.escalaralcoiaicomtat.app.ui.reusable.ReportButton
import org.escalaralcoiaicomtat.app.utils.format
import org.escalaralcoiaicomtat.app.utils.unit.meters
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val sidePathInformationPanelHeight: Dp = 500.dp
private val sidePathInformationPanelMaxWidth: Dp = 500.dp

@Composable
fun PathsScreen(
    sectorId: Long,
    highlightPathId: Long?,
    onBackRequested: () -> Unit,
    onReportRequested: (Path?) -> Unit,
    onEditSectorRequested: (() -> Unit)?,
    onEditPathRequested: ((Path) -> Unit)?,
    onCreatePathRequested: (() -> Unit)?,
    onPreviousSectorRequested: ((id: Long) -> Unit),
    onNextSectorRequested: ((id: Long) -> Unit),
    viewModel: PathsScreenModel = viewModel { PathsScreenModel() }
) {
    val sector by viewModel.parent.collectAsState()
    val paths by viewModel.children.collectAsState()
    val blocks by viewModel.blocks.collectAsState()
    val selectedPath by viewModel.displayingChild.collectAsState()
    val editingBlocking by viewModel.editingBlocking.collectAsState()
    val isLoadingBlockingEdit by viewModel.isLoadingBlockingEdit.collectAsState()
    val nextParentId by viewModel.nextParentId.collectAsState()
    val previousParentId by viewModel.previousParentId.collectAsState()

    LaunchedEffect(sectorId) {
        viewModel.load(sectorId) {
            Napier.w { "Could not find sector with id $sectorId" }
            onBackRequested()
        }
    }

    PathsScreen(
        sector = sector,
        paths = paths,
        blocks = blocks,
        selectedPath = selectedPath,
        highlightPathId = highlightPathId,
        editingBlocking = editingBlocking,
        isLoadingBlockingEdit = isLoadingBlockingEdit,
        onBlockingDeleteRequested = viewModel::deleteBlocking,
        onBlockingSaveRequested = viewModel::saveBlocking,
        onBackRequested = onBackRequested,
        onReportRequested = onReportRequested,
        onEditSectorRequested = onEditSectorRequested,
        onEditPathRequested = onEditPathRequested,
        onCreatePathRequested = onCreatePathRequested,
        onEditBlockingRequested = viewModel::editBlocking.takeIf { onCreatePathRequested != null },
        onEditBlockingStopRequested = viewModel::stopEditingBlocking,
        onPathClicked = viewModel::selectChild,
        onPreviousSectorRequested = onPreviousSectorRequested.takeIf { previousParentId != null }?.let {
            { it(previousParentId!!) }
        },
        onNextSectorRequested = onNextSectorRequested.takeIf { nextParentId != null }?.let {
            { it(nextParentId!!) }
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
private fun PathsScreen(
    sector: Sector?,
    paths: List<Path>?,
    blocks: List<Blocking>?,
    selectedPath: Path?,
    highlightPathId: Long?,
    editingBlocking: Blocking?,
    isLoadingBlockingEdit: Boolean,
    onBlockingDeleteRequested: () -> Unit,
    onBlockingSaveRequested: () -> Unit,
    onBackRequested: () -> Unit,
    onReportRequested: (Path?) -> Unit,
    onEditSectorRequested: (() -> Unit)?,
    onEditPathRequested: ((Path) -> Unit)?,
    onCreatePathRequested: (() -> Unit)?,
    onEditBlockingRequested: ((Blocking) -> Unit)?,
    onEditBlockingStopRequested: () -> Unit,
    onPathClicked: (path: Path?) -> Unit,
    onPreviousSectorRequested: (() -> Unit)?,
    onNextSectorRequested: (() -> Unit)?,
) {
    var showingBottomSheet by remember { mutableStateOf(false) }
    if (showingBottomSheet && sector != null) {
        SectorInformationBottomSheet(sector) { showingBottomSheet = false }
    }

    editingBlocking?.let {
        EditBlockingDialog(
            blocking = it,
            onBlockingChange = onEditBlockingRequested ?: {},
            isLoading = isLoadingBlockingEdit,
            onDeleteRequested = onBlockingDeleteRequested,
            onSaveRequested = onBlockingSaveRequested,
            onDismissRequested = onEditBlockingStopRequested,
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    sector?.let {
                        Text(
                            text = it.displayName,
                            modifier = Modifier.sharedElement("sector-${it.id}"),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackRequested) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                actions = {
                    ReportButton { onReportRequested(null) }
                    IconButton(
                        onClick = { showingBottomSheet = true }
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            stringResource(Res.string.sector_information_title)
                        )
                    }
                    if (onEditSectorRequested != null) {
                        IconButton(
                            onClick = onEditSectorRequested,
                        ) {
                            Icon(Icons.Default.Edit, stringResource(Res.string.editor_edit))
                        }
                    }
                    if (onCreatePathRequested != null) {
                        IconButton(
                            onClick = onCreatePathRequested,
                        ) {
                            Icon(Icons.Outlined.Add, stringResource(Res.string.editor_create))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        PathsList(
            sector,
            paths,
            blocks,
            selectedPath,
            highlightPathId,
            modifier = Modifier.padding(paddingValues),
            onReportRequested = onReportRequested,
            onEditRequested = onEditPathRequested,
            onEditBlockingRequested = onEditBlockingRequested,
            onPathClicked = onPathClicked,
            onPreviousSectorRequested = onPreviousSectorRequested,
            onNextSectorRequested = onNextSectorRequested,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SectorInformationBottomSheet(sector: Sector, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = stringResource(Res.string.sector_information_title),
                style = MaterialTheme.typography.titleLarge
            )
            MetaCard(
                icon = sector.sunTime.icon(),
                text = sector.sunTime.label(),
                message = sector.sunTime.message(),
                modifier = Modifier.padding(vertical = 4.dp),
            )
            sector.point?.let { point ->
                MetaCard(
                    icon = Icons.Default.LocationOn,
                    text = stringResource(Res.string.sector_information_location),
                    message = "${point.latitude}, ${point.longitude}",
                    modifier = Modifier.padding(vertical = 4.dp),
                ) { launchPoint(point, sector.displayName) }
            }

            val gpxDownloadUrl = sector.getGPXDownloadUrl()
            if (sector.walkingTime != null) {
                MetaCard(
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    text = stringResource(Res.string.sector_walking_time),
                    message = pluralStringResource(
                        Res.plurals.sector_walking_time_description,
                        sector.walkingTime.toInt(),
                        sector.walkingTime
                    ) + (gpxDownloadUrl?.let {
                        // If there's a GPX, add the info text
                        "\n\n" + stringResource(Res.string.sector_track_description)
                    } ?: ""),
                    modifier = Modifier.padding(vertical = 4.dp),
                    onClick = gpxDownloadUrl?.let { gpx ->
                        { launchUrl(gpx) }
                    }
                )
            } else if (gpxDownloadUrl != null) {
                MetaCard(
                    icon = Icons.Default.Route,
                    text = stringResource(Res.string.sector_track_title),
                    message = stringResource(Res.string.sector_track_description),
                    modifier = Modifier.padding(vertical = 4.dp),
                    onClick = { launchUrl(gpxDownloadUrl) }
                )
            }

            for (track in sector.tracks.orEmpty()) {
                val url = try {
                    Url(track.url)
                } catch (_: URLParserException) {
                    continue
                }
                MetaCard(
                    icon = track.type.icon,
                    text = stringResource(
                        Res.string.sector_track_external_title,
                        track.type.displayName
                    ),
                    message = stringResource(Res.string.sector_track_external_description),
                    modifier = Modifier.padding(vertical = 4.dp),
                    onClick = { launchUrl(url) }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
fun PathsList(
    sector: Sector?,
    paths: List<Path>?,
    blocks: List<Blocking>?,
    selectedPath: Path?,
    highlightPathId: Long?,
    modifier: Modifier = Modifier,
    onReportRequested: (Path) -> Unit,
    onEditRequested: ((Path) -> Unit)?,
    onEditBlockingRequested: ((Blocking) -> Unit)?,
    onPathClicked: (path: Path?) -> Unit,
    onPreviousSectorRequested: (() -> Unit)?,
    onNextSectorRequested: (() -> Unit)?,
) {
    val windowSizeClass = calculateWindowSizeClass()

    AnimatedContent(
        targetState = sector,
        transitionSpec = {
            val enter = if (initialState == null) {
                slideInHorizontally { it }
            } else {
                fadeIn()
            }
            val exit = if (targetState == null) {
                slideOutHorizontally { -it }
            } else {
                fadeOut()
            }
            enter togetherWith exit
        },
        modifier = modifier
    ) { parent ->
        if (parent == null) {
            CircularProgressIndicatorBox()
        } else {
            val shouldDisplaySidePanel =
                windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                if (shouldDisplaySidePanel) {
                    Column(
                        modifier = Modifier.fillMaxHeight().weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PathsListView(
                            paths = paths,
                            blocks = blocks,
                            highlightPathId = highlightPathId,
                            modifier = Modifier.fillMaxHeight().weight(1f),
                            onEditRequested = onEditRequested,
                            onPathClicked = onPathClicked
                        )

                        AnimatedContent(
                            targetState = selectedPath,
                            transitionSpec = {
                                slideInVertically { it } togetherWith slideOutVertically { it }
                            }
                        ) { path ->
                            if (path != null) {
                                LazyColumn(
                                    modifier = Modifier
                                        .widthIn(max = sidePathInformationPanelMaxWidth)
                                        .fillMaxWidth()
                                        .heightIn(max = sidePathInformationPanelHeight)
                                        .clip(
                                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                        )
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                ) {
                                    bottomSheetContents(
                                        path,
                                        blocks = blocks.orEmpty().filter { it.pathId == path.id },
                                        isModal = false,
                                        onReportRequested = { onReportRequested(path) },
                                        onEditRequested = onEditRequested?.let { { it(path) } },
                                        onEditBlockingRequested = onEditBlockingRequested,
                                        onDismissRequested = { onPathClicked(null) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    selectedPath?.let { path ->
                        ModalBottomSheet(
                            onDismissRequest = { onPathClicked(null) }
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            ) {
                                bottomSheetContents(
                                    path,
                                    blocks = blocks.orEmpty().filter { it.pathId == path.id },
                                    isModal = true,
                                    onReportRequested = { onReportRequested(path) },
                                    onEditRequested = onEditRequested?.let { { it(path) } },
                                    onEditBlockingRequested = onEditBlockingRequested,
                                ) { onPathClicked(null) }
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        SectorImageView(
                            sector = parent,
                            modifier = Modifier.fillMaxSize()
                        )

                        Row(
                            modifier = Modifier.padding(8.dp).align(Alignment.BottomEnd)
                        ) {
                            onPreviousSectorRequested?.let {
                                SmallFloatingActionButton(
                                    onClick = it,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = stringResource(Res.string.sector_previous),
                                    )
                                }
                            }
                            onNextSectorRequested?.let {
                                SmallFloatingActionButton(
                                    onClick = it,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = stringResource(Res.string.sector_next),
                                    )
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = !shouldDisplaySidePanel
                    ) {
                        PathsListView(
                            paths = paths,
                            blocks = blocks,
                            highlightPathId = highlightPathId,
                            modifier = Modifier.fillMaxWidth()
                                .fillMaxHeight(0.3f)
                                .weight(1f),
                            onEditRequested = onEditRequested,
                            onPathClicked = onPathClicked
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectorImageView(
    sector: Sector,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(sector.imageUrl())
    val state by painter.state.collectAsState()

    AnimatedContent(
        targetState = state,
        modifier = modifier,
    ) { painterState ->
        when (painterState) {
            is AsyncImagePainter.State.Empty,
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicatorBox()
            }

            is AsyncImagePainter.State.Success -> {
                ZoomImage(
                    painter = painter,
                    contentDescription = sector.displayName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            is AsyncImagePainter.State.Error -> {
                ImageLoadError(
                    throwable = painterState.result.throwable,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PathsListView(
    paths: List<Path>?,
    blocks: List<Blocking>?,
    highlightPathId: Long?,
    modifier: Modifier = Modifier,
    onEditRequested: ((Path) -> Unit)?,
    onPathClicked: (path: Path) -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(highlightPathId, paths) {
        if (highlightPathId == null) return@LaunchedEffect
        if (paths == null) return@LaunchedEffect
        val index = paths.indexOfFirst { it.id == highlightPathId }
        if (index < 0) return@LaunchedEffect
        scope.launch { listState.animateScrollToItem(index) }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(paths ?: emptyList()) { path ->
            ContextMenu(
                enabled = onEditRequested != null,
                items = listOf(
                    ContextMenuItem(
                        label = { stringResource(Res.string.editor_edit) },
                        onClick = { onEditRequested?.invoke(path) }
                    )
                ),
            ) {
                PathListItem(
                    path = path,
                    pathBlocks = blocks.orEmpty().filter { it.pathId == path.id },
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    highlight = highlightPathId == path.id
                ) { onPathClicked(path) }
            }
        }
    }
}

@OptIn(ExperimentalSettingsApi::class)
private fun LazyListScope.bottomSheetContents(
    child: Path,
    blocks: List<Blocking>,
    isModal: Boolean,
    onReportRequested: () -> Unit,
    onEditRequested: (() -> Unit)?,
    onEditBlockingRequested: ((Blocking) -> Unit)?,
    onDismissRequested: () -> Unit
) {
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = child.sketchId.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(
                text = child.displayName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            ReportButton(onReportRequested)
            if (onEditBlockingRequested != null) {
                IconButton(
                    onClick = { onEditBlockingRequested(Blocking.new(pathId = child.id)) },
                ) {
                    Icon(Icons.Default.NotificationAdd, stringResource(Res.string.path_blocking_create))
                }
            }
            if (onEditRequested != null) {
                IconButton(
                    onClick = onEditRequested,
                ) {
                    Icon(Icons.Default.Edit, stringResource(Res.string.editor_edit))
                }
            }
            if (!isModal) {
                IconButton(
                    onClick = onDismissRequested
                ) {
                    Icon(Icons.Rounded.Close, stringResource(Res.string.action_close))
                }
            }
        }
    }
    items(blocks) { blocking ->
        MetaCard(
            icon = blocking.type.icon,
            text = stringResource(Res.string.path_blocking_title),
            message = StringBuilder().apply {
                appendLine(stringResource(blocking.type.message))
                blocking.endDate?.let { endDate ->
                    append(
                        stringResource(Res.string.path_blocking_date, endDate.toString())
                    )
                }
                blocking.recurrence?.let { recurrence ->
                    append(
                        stringResource(
                            Res.string.path_blocking_recurrent,
                            recurrence.from(),
                            recurrence.to(),
                        )
                    )
                }
            }.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = blocking.type.cardColors(),
            onClick = onEditBlockingRequested?.let { { it(blocking) } },
        )
    }
    if (child.height != null) item {
        val localUnitsConfiguration = LocalUnitsConfiguration.current
        MetaCard(
            icon = Icons.Filled.Rope,
            text = stringResource(Res.string.path_height),
            bigText = with(localUnitsConfiguration) {
                child.height.meters.asDistanceValue()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
    if ((child.grade != null && child.grade != SportsGrade.UNKNOWN) || child.aidGrade != null) item {
        MetaCard(
            icon = Icons.Filled.ClimbingShoes,
            text = stringResource(Res.string.path_grade),
            bigText = child.gradeAnnotatedString(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
    countMetaCard(child.safes)
    if (child.ending != null) item {
        MetaCard(
            icon = Icons.Filled.SwipeDownAlt,
            text = stringResource(Res.string.path_ending),
            bigText = stringResource(child.ending.displayName),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
    if (!child.pitches.isNullOrEmpty()) item {
        val pitches = child.pitches.sortedBy { it.pitch }
        MetaCard(
            icon = Icons.AutoMirrored.Filled.ListAlt,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            content = {
                for (pitch in pitches) {
                    PitchInfoRow(
                        pitch,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (pitches.lastOrNull() != pitch) HorizontalDivider()
                }
            },
        )
    }
    val pathBuilder = child.builder?.orNull()
    val pathReBuilders = child.reBuilders?.mapNotNull { it.orNull() }
    if (pathBuilder != null || pathReBuilders?.isNotEmpty() == true) item {
        var text by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            val name = pathBuilder?.name
            val date = pathBuilder?.date
            text = StringBuilder().apply {
                if (name != null || date != null) {
                    appendLine(
                        getString(Res.string.path_builder_message).format(
                            if (name != null && date != null) {
                                getString(Res.string.path_builder_name_date).format(name, date)
                            } else if (name == null && date != null) {
                                getString(Res.string.path_builder_date).format(date)
                            } else if (name != null && date == null) {
                                getString(Res.string.path_builder_name).format(name)
                            } else {
                                "" // never reached
                            }
                        )
                    )
                }
                pathReBuilders?.forEach { builder ->
                    appendLine(
                        getString(Res.string.path_re_builder_message).format(
                            if (builder.name != null && builder.date != null) {
                                getString(Res.string.path_builder_name_date)
                                    .format(builder.name, builder.date)
                            } else if (builder.name == null && builder.date != null) {
                                getString(Res.string.path_builder_date)
                                    .format(builder.date)
                            } else if (builder.name != null && builder.date == null) {
                                getString(Res.string.path_builder_name)
                                    .format(builder.name)
                            } else {
                                "" // never reached
                            }
                        )
                    )
                }
            }.toString()
        }
        MetaCard(
            icon = Icons.Filled.ClimbingHelmet,
            text = text,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
    }

    if (child.showDescription && child.description != null) item {
        MetaCard(
            icon = Icons.Filled.Description,
            textMarkdown = child.description,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
    }
}

private fun LazyListScope.countMetaCard(safes: SafesCount) {
    if (safes.isNotNull) item {
        MetaCard(
            icon = Icons.Filled.ClimbingAnchor,
            text = stringResource(Res.string.path_quickdraws_title),
            bigText = safes.stringCount?.toInt().let {
                stringResource(
                    Res.string.path_quickdraws,
                    if (it == null || it <= 0) "¿?" else it.toString()
                )
            },
            dialogText = safes.toAnnotatedString(),
        )
    }
}

@Preview
@Composable
private fun countMetaCard_parabolts_unknownAmount_Preview() {
    LazyColumn {
        countMetaCard(
            SafesCount(paraboltCount = 0u)
        )
    }
}

@Preview
@Composable
private fun SafesCount_toAnnotatedString_parabolts_unknownAmount_Preview() {
    Text(
        SafesCount(paraboltCount = 0u).toAnnotatedString()!!
    )
}

@Preview
@Composable
private fun SafesCount_toAnnotatedString_parabolts_amount_Preview() {
    Text(
        SafesCount(paraboltCount = 5u).toAnnotatedString()!!
    )
}

@Preview
@Composable
private fun SafesCount_toAnnotatedString_multiple_unknownAmount_Preview() {
    Text(
        SafesCount(paraboltCount = 0u, burilCount = 0u).toAnnotatedString()!!
    )
}

@Preview
@Composable
private fun SafesCount_toAnnotatedString_multiple_amount_Preview() {
    Text(
        SafesCount(paraboltCount = 5u, burilCount = 7u).toAnnotatedString()!!
    )
}

@Preview
@Composable
private fun SafesCount_toAnnotatedString_multiple_unknownAndAmount_Preview() {
    Text(
        SafesCount(paraboltCount = 0u, burilCount = 7u).toAnnotatedString()!!
    )
}


@Composable
private fun MetaCard(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    text: String? = null,
    textMarkdown: String? = null,
    iconContentDescription: String? = null,
    bigText: String? = null,
    bigTextColor: Color = Color.Unspecified,
    dialogText: AnnotatedString? = null,
    message: String? = null,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    content: (@Composable ColumnScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    MetaCard(
        icon = icon,
        modifier = modifier,
        text = text,
        textMarkdown = textMarkdown,
        iconContentDescription = iconContentDescription,
        bigText = bigText?.let {
            buildAnnotatedString {
                withStyle(SpanStyle(color = bigTextColor)) { append(bigText) }
            }
        },
        dialogText = dialogText,
        message = message,
        colors = colors,
        content = content,
        onClick = onClick,
    )
}

@Composable
private fun MetaCard(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    text: String? = null,
    textMarkdown: String? = null,
    iconContentDescription: String? = null,
    bigText: AnnotatedString? = null,
    dialogText: AnnotatedString? = null,
    message: String? = null,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    content: (@Composable ColumnScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    var showingDialog by remember { mutableStateOf(false) }
    if (showingDialog && dialogText != null) {
        AlertDialog(
            onDismissRequest = { showingDialog = false },
            title = { Text(stringResource(Res.string.dialog_more_information)) },
            text = { Text(dialogText) },
            confirmButton = {
                TextButton(onClick = { showingDialog = false }) {
                    Text(stringResource(Res.string.action_close))
                }
            }
        )
    }

    OutlinedCard(
        modifier = Modifier
            .clickable(
                enabled = dialogText != null || onClick != null
            ) {
                if (dialogText != null) {
                    showingDialog = true
                } else if (onClick != null) {
                    onClick()
                }
            }
            .then(modifier),
        colors = colors,
    ) {
        Row {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription,
                modifier = Modifier
                    .padding(8.dp)
                    .size(36.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp, end = 12.dp)
                    .padding(vertical = 8.dp)
            ) {
                if (text != null) {
                    Text(
                        text = text,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (textMarkdown != null) {
                    Markdown(
                        content = textMarkdown,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (bigText != null) {
                    Text(
                        text = bigText,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                if (dialogText != null) {
                    Text(
                        text = stringResource(Res.string.action_tap_to_see_more),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
                if (message != null) {
                    Text(
                        text = message,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                content?.invoke(this)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun PitchInfoRow(
    pitch: PitchInfo,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Badge {
                Text("L${pitch.pitch}")
            }
            if (pitch.grade != null || pitch.aidGrade != null) {
                Text(
                    text = pitch.gradeAnnotatedString(),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                )
            }
            pitch.height?.let { height ->
                val localUnitsConfiguration = LocalUnitsConfiguration.current
                Text(
                    text = with(localUnitsConfiguration) {
                        height.meters.asDistanceValue()
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                )
            }
            pitch.ending?.let { ending ->
                Text(
                    text = stringResource(ending.displayName),
                    modifier = Modifier.weight(3f),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            pitch.info?.let { info ->
                Image(info.icon, stringResource(info.stringRes))
                Text(
                    text = stringResource(info.stringRes),
                    modifier = Modifier.weight(1f),
                )
            }
            pitch.inclination?.let { inclination ->
                Image(inclination.icon, stringResource(inclination.stringRes))
                Text(
                    text = stringResource(inclination.stringRes),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
