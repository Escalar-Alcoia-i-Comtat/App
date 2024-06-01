package ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ui.composition.LocalUnitsConfiguration
import ui.icons.ClimbingAnchor
import ui.icons.ClimbingShoes
import ui.icons.Rope
import ui.list.PathListItem
import ui.model.AppScreenModel
import ui.model.DataScreenModel
import ui.model.PathsScreenModel
import ui.platform.getScreenSize
import ui.screen.DataScreen.SidePanelContents
import utils.format
import utils.unit.meters

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
class PathsScreen(
    id: Long,
    private val highlightPathId: Long? = null
) : DataScreen<Sector, Path>(
    id = id,
    depth = @Suppress("MagicNumber") 3,
    { PathsScreenModel(it) },
    null
) {
    override fun shouldDisplaySidePanel(windowSizeClass: WindowSizeClass): Boolean {
        return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun ContentView(
        parentState: Sector,
        childrenState: List<Path>?,
        appScreenModel: AppScreenModel,
        model: DataScreenModel<Sector, Path>
    ) {
        // Cast the model as the correct type
        model as PathsScreenModel

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Zoomable(
                modifier = Modifier.fillMaxWidth().weight(1f).clipToBounds()
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

            val size = getScreenSize()
            val windowSizeClass = calculateWindowSizeClass()
            val shouldDisplaySidePanel = remember(windowSizeClass) {
                shouldDisplaySidePanel(windowSizeClass)
            }
            AnimatedVisibility(
                visible = !shouldDisplaySidePanel
            ) {
                PathsListView(
                    childrenState,
                    modifier = Modifier.fillMaxWidth().heightIn(max = size.height * 0.3f).weight(1f),
                    onPathClicked = model.displayingChild::tryEmit
                )
            }
        }
    }

    override val SidePanel: SidePanelContents<Sector, Path> =
        SidePanelContents { _, childrenState, _, model ->
            // Cast the model as the correct type
            model as PathsScreenModel

            PathsListView(
                childrenState,
                modifier = Modifier.fillMaxHeight().weight(1f),
                onPathClicked = model.displayingChild::tryEmit
            )
        }

    @Composable
    private fun PathsListView(
        childrenState: List<Path>?,
        modifier: Modifier = Modifier,
        onPathClicked: (path: Path) -> Unit
    ) {
        val scope = rememberCoroutineScope()
        val listState = rememberLazyListState()

        LaunchedEffect(highlightPathId, childrenState) {
            if (highlightPathId == null) return@LaunchedEffect
            if (childrenState == null) return@LaunchedEffect
            val index = childrenState.indexOfFirst { it.id == highlightPathId }
            if (index < 0) return@LaunchedEffect
            scope.launch { listState.animateScrollToItem(index) }
        }

        LazyColumn(
            modifier = modifier,
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(childrenState ?: emptyList()) { path ->
                PathListItem(
                    path,
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
    override fun ColumnScope.BottomSheetContents(
        child: Path,
        model: DataScreenModel<Sector, Path>,
        isModal: Boolean
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
                        onClick = { model.displayingChild.tryEmit(null) }
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
    fun MetaCard(
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
}
