package ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
@ExperimentalFoundationApi
@ExperimentalMaterial3WindowSizeClassApi
fun AdaptiveNavigationScaffold(
    items: List<NavigationItem>,
    initialPage: Int = 0,
    userScrollEnabled: Boolean = true,
    navigationBarVisible: Boolean = true,
    topBar: @Composable () -> Unit = {},
    topBarVisible: Boolean = true,
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable (() -> Unit)? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (page: Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage) { items.size }
    var currentPage by remember { mutableIntStateOf(initialPage) }

    val windowSizeClass = calculateWindowSizeClass()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { currentPage = it }
    }

    PermanentNavigationDrawer(
        drawerContent = {
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                AnimatedVisibility(navigationBarVisible) {
                    PermanentDrawerSheet {
                        for ((index, item) in items.withIndex()) {
                            NavigationDrawerItem(
                                selected = currentPage == index,
                                onClick = {
                                    scope.launch { pagerState.scrollToPage(index) }
                                    currentPage = index
                                },
                                icon = { Icon(item.icon(), item.label()) },
                                label = { Text(item.label()) },
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium) {
                AnimatedVisibility(navigationBarVisible) {
                    NavigationRail(
                        header = floatingActionButton?.let { fab -> { fab() } }
                    ) {
                        for ((index, item) in items.withIndex()) {
                            NavigationRailItem(
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                icon = { Icon(item.icon(), item.label()) },
                                label = { Text(item.label()) }
                            )
                        }
                    }
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxHeight().weight(1f),
                topBar = {
                    AnimatedVisibility(
                        visible = topBarVisible,
                        enter = slideInVertically { -it },
                        exit = slideOutVertically { -it }
                    ) {
                        topBar()
                    }
                },
                snackbarHost = snackbarHost,
                floatingActionButton = {
                    floatingActionButton
                        .takeUnless { windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium }
                        ?.invoke()
                },
                floatingActionButtonPosition = floatingActionButtonPosition,
                contentColor = contentColor,
                containerColor = containerColor,
                contentWindowInsets = contentWindowInsets,
                bottomBar = {
                    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                        AnimatedVisibility(
                            visible = navigationBarVisible,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it }
                        ) {
                            NavigationBar {
                                for ((index, item) in items.withIndex()) {
                                    NavigationBarItem(
                                        selected = pagerState.currentPage == index,
                                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                        icon = { Icon(item.icon(), item.label()) },
                                        label = { Text(item.label()) }
                                    )
                                }
                            }
                        }
                    }
                }
            ) { paddingValues ->
                if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        userScrollEnabled = userScrollEnabled
                    ) { page ->
                        content(page)
                    }
                } else {
                    AnimatedContent(
                        targetState = currentPage,
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        transitionSpec = {
                            slideInHorizontally {
                                if (initialState > targetState) {
                                    -it
                                } else {
                                    it
                                }
                            } togetherWith slideOutHorizontally {
                                if (initialState > targetState) {
                                    it
                                } else {
                                    -it
                                }
                            }
                        }
                    ) { page ->
                        content(page)
                    }
                }
            }
        }
    }
}
