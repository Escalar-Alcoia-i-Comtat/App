package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.DataType
import data.DataTypeWithImage
import data.Zone
import io.github.aakira.napier.Napier
import ui.list.DataCard
import ui.model.AppScreenModel
import ui.model.DataScreenModel
import ui.platform.MapComposable

abstract class DataScreen<Parent : DataTypeWithImage, ChildrenType : DataType>(
    val id: Long,
    depth: Int,
    private val modelFactory: (AppScreenModel) -> DataScreenModel<Parent, ChildrenType>,
    private val subScreenFactory: ((id: Long) -> Screen)?
) : DepthScreen(depth) {
    protected open val sidePathInformationPanelHeight: Dp = 500.dp
    protected open val sidePathInformationPanelMaxWidth: Dp = 500.dp

    /**
     * Checks whether the side panel should be displayed.
     * Defaults to always false.
     */
    open fun shouldDisplaySidePanel(windowSizeClass: WindowSizeClass): Boolean = false

    @ExperimentalMaterial3Api
    @Composable
    @ExperimentalMaterial3WindowSizeClassApi
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val appScreenModel = navigator.rememberNavigatorScreenModel { AppScreenModel() }
        val model = rememberScreenModel { modelFactory(appScreenModel) }

        val parentState: Parent? by model.parent.collectAsState(null)
        val childrenState: List<ChildrenType>? by model.children.collectAsState(null)
        val notFound: Boolean by model.notFound.collectAsState(false)

        LaunchedEffect(id) {
            model.load(id)
        }

        LaunchedEffect(notFound) {
            if (notFound) {
                Napier.w { "Could not find element with id $id" }
                navigator.pop()
            }
        }

        val windowSizeClass = calculateWindowSizeClass()

        AnimatedContent(
            targetState = parentState,
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val displayingChild: ChildrenType? by model.displayingChild.collectAsState()

                val shouldDisplaySidePanel = remember(windowSizeClass) {
                    shouldDisplaySidePanel(windowSizeClass)
                }
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (shouldDisplaySidePanel) SidePanel?.let { composable ->
                        Column(
                            modifier = Modifier.fillMaxHeight().weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            with(composable) {
                                Content(parent, childrenState, appScreenModel, model)
                            }

                            AnimatedContent(
                                targetState = displayingChild,
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
                                        BottomSheetContents(it, model, false)
                                    }
                                }
                            }
                        }
                    } else {
                        displayingChild?.let {
                            ModalBottomSheet(
                                onDismissRequest = { model.displayingChild.tryEmit(null) }
                            ) {
                                BottomSheetContents(it, model, true)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxHeight().weight(1f)
                    ) {
                        ContentView(parent, childrenState, appScreenModel, model)
                    }
                }
            }
        }
    }

    @Composable
    open fun ContentView(
        parentState: Parent,
        childrenState: List<ChildrenType>?,
        appScreenModel: AppScreenModel,
        model: DataScreenModel<Parent, ChildrenType>
    ) {
        val navigator = LocalNavigator.current

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (parentState is Zone) {
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
                        kmzUUID = parentState.kmzUUID
                    )
                }
            }
            items(childrenState ?: emptyList()) { child ->
                if (child is DataTypeWithImage) {
                    DataCard(
                        item = child,
                        imageHeight = 200.dp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 12.dp)
                            .widthIn(max = 600.dp)
                            .fillMaxWidth()
                    ) {
                        val screen = subScreenFactory?.let { it(child.id) }
                        screen?.let { navigator?.push(screen) }
                    }
                }
            }
        }
    }

    /**
     * Only visible if [shouldDisplaySidePanel] returns `true`.
     * Should adapt to the space available with [RowScope.weight], for example.
     */
    @Suppress("VariableNaming")
    open val SidePanel: SidePanelContents<Parent, ChildrenType>? = null

    @Composable
    open fun ColumnScope.BottomSheetContents(
        child: ChildrenType,
        model: DataScreenModel<Parent, ChildrenType>,
        isModal: Boolean
    ) {
    }

    fun interface SidePanelContents<Parent : DataTypeWithImage, ChildrenType : DataType> {
        @Composable
        fun ColumnScope.Content(
            parentState: Parent,
            childrenState: List<ChildrenType>?,
            appScreenModel: AppScreenModel,
            model: DataScreenModel<Parent, ChildrenType>
        )
    }
}
