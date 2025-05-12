package org.escalaralcoiaicomtat.app.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SwipeDownAlt
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clipToBounds
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
import com.mxalbert.zoomable.Zoomable
import com.russhwolf.settings.ExperimentalSettingsApi
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.aakira.napier.Napier
import io.ktor.http.URLParserException
import io.ktor.http.Url
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.generic.SportsGrade
import org.escalaralcoiaicomtat.app.data.generic.color
import org.escalaralcoiaicomtat.app.platform.BackHandler
import org.escalaralcoiaicomtat.app.platform.launchPoint
import org.escalaralcoiaicomtat.app.platform.launchUrl
import org.escalaralcoiaicomtat.app.ui.composition.LocalUnitsConfiguration
import org.escalaralcoiaicomtat.app.ui.icons.ClimbingAnchor
import org.escalaralcoiaicomtat.app.ui.icons.ClimbingHelmet
import org.escalaralcoiaicomtat.app.ui.icons.ClimbingShoes
import org.escalaralcoiaicomtat.app.ui.icons.Rope
import org.escalaralcoiaicomtat.app.ui.list.PathListItem
import org.escalaralcoiaicomtat.app.ui.model.PathsScreenModel
import org.escalaralcoiaicomtat.app.ui.reusable.CircularProgressIndicatorBox
import org.escalaralcoiaicomtat.app.ui.reusable.ContextMenu
import org.escalaralcoiaicomtat.app.utils.format
import org.escalaralcoiaicomtat.app.utils.unit.meters
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private val sidePathInformationPanelHeight: Dp = 500.dp
private val sidePathInformationPanelMaxWidth: Dp = 500.dp

@Composable
fun PathsScreen(
    sectorId: Long,
    highlightPathId: Long?,
    onBackRequested: () -> Unit,
    onEditSectorRequested: (() -> Unit)?,
    onEditPathRequested: ((Path) -> Unit)?,
    onCreatePathRequested: (() -> Unit)?,
    viewModel: PathsScreenModel = viewModel { PathsScreenModel() }
) {
    val sector by viewModel.parent.collectAsState()
    val paths by viewModel.children.collectAsState()
    val selectedPath by viewModel.displayingChild.collectAsState()

    LaunchedEffect(sectorId) {
        viewModel.load(sectorId) {
            Napier.w { "Could not find sector with id $sectorId" }
            onBackRequested()
        }
    }

    BackHandler(onBack = onBackRequested)

    PathsScreen(
        sector = sector,
        paths = paths,
        selectedPath = selectedPath,
        highlightPathId = highlightPathId,
        onBackRequested = onBackRequested,
        onEditSectorRequested = onEditSectorRequested,
        onEditPathRequested = onEditPathRequested,
        onCreatePathRequested = onCreatePathRequested,
        onPathClicked = viewModel::selectChild
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PathsScreen(
    sector: Sector?,
    paths: List<Path>?,
    selectedPath: Path?,
    highlightPathId: Long?,
    onBackRequested: () -> Unit,
    onEditSectorRequested: (() -> Unit)?,
    onEditPathRequested: ((Path) -> Unit)?,
    onCreatePathRequested: (() -> Unit)?,
    onPathClicked: (path: Path?) -> Unit
) {
    var showingBottomSheet by remember { mutableStateOf(false) }
    if (showingBottomSheet && sector != null) {
        SectorInformationBottomSheet(sector) { showingBottomSheet = false }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { sector?.let { Text(it.displayName) } },
                navigationIcon = {
                    IconButton(onClick = onBackRequested) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                actions = {
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
            selectedPath,
            highlightPathId,
            modifier = Modifier.padding(paddingValues),
            onEditRequested = onEditPathRequested,
            onPathClicked = onPathClicked
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
    selectedPath: Path?,
    highlightPathId: Long?,
    modifier: Modifier = Modifier,
    onEditRequested: ((Path) -> Unit)?,
    onPathClicked: (path: Path?) -> Unit
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
                                Column(
                                    modifier = Modifier
                                        .widthIn(max = sidePathInformationPanelMaxWidth)
                                        .fillMaxWidth()
                                        .heightIn(max = sidePathInformationPanelHeight)
                                        .clip(
                                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                        )
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    BottomSheetContents(
                                        path,
                                        false,
                                        onEditRequested = onEditRequested?.let { { it(path) } },
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
                            BottomSheetContents(
                                path,
                                isModal = true,
                                onEditRequested = onEditRequested?.let { { it(path) } },
                            ) { onPathClicked(null) }
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f)
                ) {
                    Zoomable(
                        modifier = Modifier.fillMaxWidth().weight(1f).clipToBounds()
                    ) {
                        val painter = rememberAsyncImagePainter(parent.imageUrl())
                        val state by painter.state.collectAsState()

                        when (state) {
                            is AsyncImagePainter.State.Empty,
                            is AsyncImagePainter.State.Loading -> {
                                CircularProgressIndicator()
                            }

                            is AsyncImagePainter.State.Success -> {
                                val (width, height) = painter.intrinsicSize
                                Image(
                                    painter = painter,
                                    contentDescription = parent.displayName,
                                    modifier = Modifier
                                        .aspectRatio(width / height)
                                        .fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            is AsyncImagePainter.State.Error -> {
                                // Show some error UI.
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = !shouldDisplaySidePanel
                    ) {
                        PathsListView(
                            paths = paths,
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
@OptIn(ExperimentalMaterial3Api::class)
private fun PathsListView(
    paths: List<Path>?,
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
                dropdownContent = {
                    ListItem(
                        headlineContent = {
                            Text(stringResource(Res.string.editor_edit))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditRequested?.invoke(path) }
                    )
                },
            ) {
                PathListItem(
                    path = path,
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

@Composable
@OptIn(ExperimentalSettingsApi::class)
private fun BottomSheetContents(
    child: Path,
    isModal: Boolean,
    onEditRequested: (() -> Unit)?,
    onDismissRequested: () -> Unit
) {
    val localUnitsConfiguration = LocalUnitsConfiguration.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = child.displayName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
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
                    Icon(Icons.Rounded.Close, null)
                }
            }
        }
        child.height?.let { height ->
            MetaCard(
                icon = Icons.Filled.Rope,
                text = stringResource(Res.string.path_height),
                bigText = with(localUnitsConfiguration) {
                    height.meters.asDistanceValue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
        child.grade?.takeIf { it != SportsGrade.UNKNOWN }?.let { grade ->
            MetaCard(
                icon = Icons.Filled.ClimbingShoes,
                text = stringResource(Res.string.path_grade),
                bigText = grade.toString(),
                bigTextColor = grade.color.current,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
        CountMetaCard(child)
        child.ending?.let { ending ->
            MetaCard(
                icon = Icons.Filled.SwipeDownAlt,
                text = stringResource(Res.string.path_ending),
                bigText = stringResource(ending.displayName),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
        val pathBuilder = child.builder?.orNull()
        val pathReBuilders = child.reBuilders?.mapNotNull { it.orNull() }
        if (pathBuilder != null || pathReBuilders?.isNotEmpty() == true) {
            val name = pathBuilder?.name
            val date = pathBuilder?.date

            val text = StringBuilder()
            if (name != null || date != null) {
                text.appendLine(
                    stringResource(Res.string.path_builder_message).format(
                        if (name != null && date != null) {
                            stringResource(Res.string.path_builder_name_date).format(name, date)
                        } else if (name == null && date != null) {
                            stringResource(Res.string.path_builder_date).format(date)
                        } else if (name != null && date == null) {
                            stringResource(Res.string.path_builder_name).format(name)
                        } else {
                            "" // never reached
                        }
                    )
                )
            }
            pathReBuilders?.forEach { builder ->
                text.appendLine(
                    stringResource(Res.string.path_re_builder_message).format(
                        if (builder.name != null && builder.date != null) {
                            stringResource(Res.string.path_builder_name_date)
                                .format(builder.name, builder.date)
                        } else if (builder.name == null && builder.date != null) {
                            stringResource(Res.string.path_builder_date)
                                .format(builder.date)
                        } else if (builder.name != null && builder.date == null) {
                            stringResource(Res.string.path_builder_name)
                                .format(builder.name)
                        } else {
                            "" // never reached
                        }
                    )
                )
            }

            MetaCard(
                icon = Icons.Filled.ClimbingHelmet,
                text = text.toString(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    child.description?.takeIf { child.showDescription }?.let { description ->
        MetaCard(
            icon = Icons.Filled.Description,
            text = description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun CountMetaCard(path: Path) {
    if (path.hasAnyCount) {
        MetaCard(
            icon = Icons.Filled.ClimbingAnchor,
            text = stringResource(Res.string.path_quickdraws_title),
            bigText = path.stringCount?.toInt()?.let {
                stringResource(Res.string.path_quickdraws)
                    .format(if (it <= 0) "¿?" else it.toString())
            },
            dialogText = if (path.hasAnyTypeCount) {
                buildAnnotatedString {
                    appendLine(stringResource(Res.string.path_safes_count))

                    @Composable
                    fun add(amount: UInt?, singleRes: StringResource, countRes: StringResource) {
                        amount?.toInt()
                            ?.takeIf { it > 0 }
                            ?.let {
                                if (it <= 1 || it >= Int.MAX_VALUE) stringResource(singleRes)
                                else stringResource(countRes).format(it)
                            }
                            ?.let { line ->
                                append('•')
                                append(' ')
                                withStyle(
                                    SpanStyle(fontWeight = FontWeight.Bold)
                                ) {
                                    appendLine(line)
                                }
                            }
                    }
                    add(
                        amount = path.paraboltCount,
                        singleRes = Res.string.path_safes_parabolts,
                        countRes = Res.string.path_safes_parabolts_count
                    )
                    add(
                        amount = path.burilCount,
                        singleRes = Res.string.path_safes_burils,
                        countRes = Res.string.path_safes_burils_count
                    )
                    add(
                        amount = path.pitonCount,
                        singleRes = Res.string.path_safes_pitons,
                        countRes = Res.string.path_safes_pitons_count
                    )
                    add(
                        amount = path.spitCount,
                        singleRes = Res.string.path_safes_spits,
                        countRes = Res.string.path_safes_spits_count
                    )
                    add(
                        amount = path.tensorCount,
                        singleRes = Res.string.path_safes_tensors,
                        countRes = Res.string.path_safes_tensors_count
                    )
                }
            } else {
                null
            }
        )
    }
}

@Composable
private fun MetaCard(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    iconContentDescription: String? = null,
    bigText: String? = null,
    bigTextColor: Color = Color.Unspecified,
    dialogText: AnnotatedString? = null,
    message: String? = null,
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
            .then(modifier)
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
                Text(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge
                )
                bigText?.let { text ->
                    Text(
                        text = text,
                        color = bigTextColor,
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
            }
        }
    }
}
