package ui.screen

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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cache.ImageCache
import com.mxalbert.zoomable.Zoomable
import com.russhwolf.settings.ExperimentalSettingsApi
import data.Path
import data.Sector
import data.generic.SportsGrade
import data.generic.color
import escalaralcoiaicomtat.composeapp.generated.resources.Res
import escalaralcoiaicomtat.composeapp.generated.resources.path_grade
import escalaralcoiaicomtat.composeapp.generated.resources.path_height
import escalaralcoiaicomtat.composeapp.generated.resources.path_quickdraws
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_burils
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_burils_count
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_count
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_none
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_parabolts
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_parabolts_count
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_pitons
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_pitons_count
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_spits
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_spits_count
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_tensors
import escalaralcoiaicomtat.composeapp.generated.resources.path_safes_tensors_count
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ui.composition.LocalNavController
import ui.composition.LocalUnitsConfiguration
import ui.icons.ClimbingAnchor
import ui.icons.ClimbingShoes
import ui.icons.Rope
import ui.list.PathListItem
import ui.model.PathsScreenModel
import ui.platform.getScreenSize
import ui.reusable.CircularProgressIndicatorBox
import utils.currentOrThrow
import utils.format
import utils.unit.meters

private val sidePathInformationPanelHeight: Dp = 500.dp
private val sidePathInformationPanelMaxWidth: Dp = 500.dp

@Composable
fun PathsScreen(
    sectorId: Long,
    highlightPathId: Long?,
    viewModel: PathsScreenModel = viewModel()
) {
    val navController = LocalNavController.currentOrThrow

    val sector by viewModel.parent.collectAsState()
    val paths by viewModel.children.collectAsState()
    val selectedPath by viewModel.displayingChild.collectAsState()

    LaunchedEffect(sectorId) {
        viewModel.load(sectorId) {
            Napier.w { "Could not find sector with id $sectorId" }
            navController.navigateUp()
        }
    }

    PathsList(
        sector,
        paths,
        selectedPath,
        highlightPathId,
        onPathClicked = viewModel::selectChild
    )
}

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
fun PathsList(
    sector: Sector?,
    paths: List<Path>?,
    selectedPath: Path?,
    highlightPathId: Long?,
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
        }
    ) { parent ->
        if (parent == null) {
            CircularProgressIndicatorBox()
        } else {
            val shouldDisplaySidePanel = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

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
                            onPathClicked = onPathClicked
                        )

                        AnimatedContent(
                            targetState = selectedPath,
                            transitionSpec = {
                                slideInVertically { it } togetherWith slideOutVertically { it }
                            }
                        ) {
                            if (it != null) {
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
                                        it,
                                        false,
                                        onDismissRequested = { onPathClicked(null) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    selectedPath?.let {
                        ModalBottomSheet(
                            onDismissRequest = { onPathClicked(null) }
                        ) {
                            BottomSheetContents(it, true) { onPathClicked(null) }
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f)
                ) {
                    Zoomable(
                        modifier = Modifier.fillMaxWidth().weight(1f).clipToBounds()
                    ) {
                        val image by ImageCache.collectStateOf(parent.image)

                        image?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = parent.displayName,
                                modifier = Modifier
                                    .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                                    .fillMaxSize()
                            )
                        } ?: CircularProgressIndicator()
                    }

                    val size = getScreenSize()

                    AnimatedVisibility(
                        visible = !shouldDisplaySidePanel
                    ) {
                        PathsListView(
                            paths = paths,
                            highlightPathId = highlightPathId,
                            modifier = Modifier.fillMaxWidth().heightIn(max = size.height * 0.3f).weight(1f),
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

@Composable
@OptIn(ExperimentalSettingsApi::class)
private fun BottomSheetContents(
    child: Path,
    isModal: Boolean,
    onDismissRequested: () -> Unit
) {
    val localUnitsConfiguration = LocalUnitsConfiguration.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = child.displayName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
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
    }
}

@Composable
private fun CountMetaCard(child: Path) {
    if (child.hasAnyCount) {
        MetaCard(
            icon = Icons.Filled.ClimbingAnchor,
            text = if (child.hasAnyTypeCount) {
                val list = mutableListOf("")
                @Composable
                fun add(amount: UInt?, singleRes: StringResource, countRes: StringResource) {
                    amount?.toInt()
                        ?.let {
                            if (it <= 0) stringResource(singleRes)
                            else stringResource(countRes).format(it)
                        }
                        ?.let(list::add)
                }
                add(child.paraboltCount, Res.string.path_safes_parabolts, Res.string.path_safes_parabolts_count)
                add(child.burilCount, Res.string.path_safes_burils, Res.string.path_safes_burils_count)
                add(child.pitonCount, Res.string.path_safes_pitons, Res.string.path_safes_pitons_count)
                add(child.spitCount, Res.string.path_safes_spits, Res.string.path_safes_spits_count)
                add(child.tensorCount, Res.string.path_safes_tensors, Res.string.path_safes_tensors_count)

                stringResource(Res.string.path_safes_count).format(list.joinToString("\n"))
            } else
                stringResource(Res.string.path_safes_none),
            bigText = child.stringCount?.toInt()?.let {
                stringResource(Res.string.path_quickdraws).format(it)
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
    bigTextColor: Color = Color.Unspecified
) {
    OutlinedCard(modifier) {
        Row {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription,
                modifier = Modifier
                    .padding(8.dp)
                    .size(48.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
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
            }
        }
    }
}
