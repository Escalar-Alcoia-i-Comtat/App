package ui.screen

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
import database.SettingsKeys
import database.settings
import escalaralcoiaicomtat.composeapp.generated.resources.Res
import escalaralcoiaicomtat.composeapp.generated.resources.action_done
import escalaralcoiaicomtat.composeapp.generated.resources.action_next
import escalaralcoiaicomtat.composeapp.generated.resources.action_skip
import escalaralcoiaicomtat.composeapp.generated.resources.action_view_video
import escalaralcoiaicomtat.composeapp.generated.resources.belayer_color
import escalaralcoiaicomtat.composeapp.generated.resources.climbing_color
import escalaralcoiaicomtat.composeapp.generated.resources.climbing_helmet_color
import escalaralcoiaicomtat.composeapp.generated.resources.drawstring_bag_color
import escalaralcoiaicomtat.composeapp.generated.resources.intro_1_message
import escalaralcoiaicomtat.composeapp.generated.resources.intro_1_title
import escalaralcoiaicomtat.composeapp.generated.resources.intro_2_message
import escalaralcoiaicomtat.composeapp.generated.resources.intro_2_title
import escalaralcoiaicomtat.composeapp.generated.resources.intro_3_message
import escalaralcoiaicomtat.composeapp.generated.resources.intro_3_title
import escalaralcoiaicomtat.composeapp.generated.resources.intro_4_message
import escalaralcoiaicomtat.composeapp.generated.resources.intro_4_title
import escalaralcoiaicomtat.composeapp.generated.resources.intro_5_message
import escalaralcoiaicomtat.composeapp.generated.resources.intro_5_title
import escalaralcoiaicomtat.composeapp.generated.resources.kid_color
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import platform.BackHandler
import platform.IntroScreenPages
import ui.composition.LocalLifecycleManager
import ui.reusable.IntroPage
import ui.reusable.icon

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
