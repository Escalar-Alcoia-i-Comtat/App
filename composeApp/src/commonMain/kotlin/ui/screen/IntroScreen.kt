package ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import database.SettingsKeys
import database.settings
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import platform.BackHandler
import platform.IntroScreenPages
import resources.MR
import ui.composition.LocalLifecycleManager
import ui.reusable.IntroPage
import ui.reusable.icon

@OptIn(ExperimentalFoundationApi::class)
class IntroScreen : Screen {
    private val pages: List<@Composable () -> Unit> = listOfNotNull(
        *IntroScreenPages.pages,
        {
            IntroPage<Any>(
                icon = painterResource(MR.images.climbing_color).icon,
                title = stringResource(MR.strings.intro_1_title),
                message = stringResource(MR.strings.intro_1_message)
            )
        },
        {
            IntroPage<Any>(
                icon = painterResource(MR.images.climbing_helmet_color).icon,
                title = stringResource(MR.strings.intro_2_title),
                message = stringResource(MR.strings.intro_2_message)
            )
        },
        {
            val uriHandler = LocalUriHandler.current
            IntroPage<Any>(
                icon = painterResource(MR.images.belayer_color).icon,
                title = stringResource(MR.strings.intro_3_title),
                message = stringResource(MR.strings.intro_3_message),
                action = object : IntroPage.Action() {
                    override val text: @Composable () -> String = {
                        stringResource(MR.strings.action_view_video)
                    }

                    override fun onClick() {
                        uriHandler.openUri(
                            "https://www.petzl.com/ES/es/Sport/Video--Asegurar-con-el-GRIGRI?ProductName=GRIGRI"
                        )
                    }
                }
            )
        },
        {
            IntroPage<Any>(
                icon = painterResource(MR.images.kid_color).icon,
                title = stringResource(MR.strings.intro_4_title),
                message = stringResource(MR.strings.intro_4_message)
            )
        },
        {
            IntroPage<Any>(
                icon = painterResource(MR.images.drawstring_bag_color).icon,
                title = stringResource(MR.strings.intro_5_title),
                message = stringResource(MR.strings.intro_5_message)
            )
        }
    )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val lifecycleManager = LocalLifecycleManager.current

        val scope = rememberCoroutineScope()

        val pagerState = rememberPagerState { pages.size }

        BackHandler {
            lifecycleManager.finish()
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (pagerState.currentPage + 1 >= pages.size) {
                            settings.putBoolean(SettingsKeys.SHOWN_INTRO, true)
                            navigator?.push(MainScreen)
                        } else scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                ) {
                    if (pagerState.currentPage + 1 < pages.size)
                        Icon(Icons.Rounded.ChevronRight, stringResource(MR.strings.action_next))
                    else
                        Icon(Icons.Rounded.Check, stringResource(MR.strings.action_done))
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TextButton(
                    onClick = {
                        settings.putBoolean(SettingsKeys.SHOWN_INTRO, true)
                        navigator?.push(MainScreen)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .zIndex(1f)
                ) {
                    Text(stringResource(MR.strings.action_skip))
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    pages[page].invoke()
                }
            }
        }
    }
}
