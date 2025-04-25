package org.escalaralcoiaicomtat.app.ui.screen

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import escalaralcoiaicomtat.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.platform.BackHandler
import org.escalaralcoiaicomtat.app.platform.IntroScreenPages
import org.escalaralcoiaicomtat.app.ui.composition.LocalLifecycleManager
import org.escalaralcoiaicomtat.app.ui.reusable.IntroPage
import org.escalaralcoiaicomtat.app.ui.reusable.icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val pages: List<@Composable () -> Unit> = listOfNotNull(
    *IntroScreenPages.pages,
    {
        IntroPage<Any>(
            icon = painterResource(Res.drawable.climbing_color).icon,
            title = stringResource(Res.string.intro_1_title),
            message = stringResource(Res.string.intro_1_message)
        )
    },
    {
        val uriHandler = LocalUriHandler.current
        IntroPage<Any>(
            icon = painterResource(Res.drawable.document).icon,
            title = stringResource(Res.string.intro_legal_title),
            message = stringResource(Res.string.intro_legal_message),
            action = object : IntroPage.Action() {
                override val text: @Composable () -> String = {
                    stringResource(Res.string.intro_legal_web)
                }

                override fun onClick() {
                    uriHandler.openUri("https://legal.escalaralcoiaicomtat.org/")
                }
            },
        )
    },
    {
        IntroPage<Any>(
            icon = painterResource(Res.drawable.climbing_helmet_color).icon,
            title = stringResource(Res.string.intro_2_title),
            message = stringResource(Res.string.intro_2_message)
        )
    },
    {
        val uriHandler = LocalUriHandler.current
        IntroPage<Any>(
            icon = painterResource(Res.drawable.belayer_color).icon,
            title = stringResource(Res.string.intro_3_title),
            message = stringResource(Res.string.intro_3_message),
            action = object : IntroPage.Action() {
                override val text: @Composable () -> String = {
                    stringResource(Res.string.action_view_video)
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
            icon = painterResource(Res.drawable.kid_color).icon,
            title = stringResource(Res.string.intro_4_title),
            message = stringResource(Res.string.intro_4_message)
        )
    },
    {
        IntroPage<Any>(
            icon = painterResource(Res.drawable.drawstring_bag_color).icon,
            title = stringResource(Res.string.intro_5_title),
            message = stringResource(Res.string.intro_5_message)
        )
    }
)

@Composable
fun IntroScreen(onIntroFinished: () -> Unit) {
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState { pages.size }

    val lifecycleManager = LocalLifecycleManager.current
    BackHandler { lifecycleManager.finish() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (pagerState.currentPage + 1 >= pages.size) {
                        settings.putBoolean(SettingsKeys.SHOWN_INTRO, true)
                        onIntroFinished()
                    } else scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            ) {
                if (pagerState.currentPage + 1 < pages.size)
                    Icon(Icons.Rounded.ChevronRight, stringResource(Res.string.action_next))
                else
                    Icon(Icons.Rounded.Check, stringResource(Res.string.action_done))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TextButton(
                enabled = pagerState.currentPage >= 2,
                onClick = {
                    settings.putBoolean(SettingsKeys.SHOWN_INTRO, true)
                    onIntroFinished()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .zIndex(1f)
            ) {
                Text(stringResource(Res.string.action_skip))
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
