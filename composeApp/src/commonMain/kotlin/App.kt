import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import network.connectivityStatus
import resources.MR
import sync.DataSync
import ui.navigation.AdaptiveNavigationScaffold
import ui.navigation.NavigationItem
import ui.screen.MainScreen

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun App() {
    DisposableEffect(Unit) {
        connectivityStatus.start()

        onDispose {
            connectivityStatus.stop()
        }
    }

    MaterialTheme {
        val isNetworkConnected by connectivityStatus.isNetworkConnected.collectAsState()

        LaunchedEffect(Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                DataSync.start()
            }
        }

        AdaptiveNavigationScaffold(
            items = listOf(
                NavigationItem(
                    label = { stringResource(MR.strings.navigation_explore) },
                    icon = { Icons.Outlined.Explore }
                ),
                NavigationItem(
                    label = { stringResource(MR.strings.navigation_settings) },
                    icon = { Icons.Outlined.Settings }
                )
            ),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Escalar Alcoià i Comtat") },
                    actions = {
                        AnimatedVisibility(
                            visible = !isNetworkConnected
                        ) {
                            PlainTooltipBox(
                                tooltip = { Text("Network not available") }
                            ) {
                                IconButton(
                                    onClick = {}
                                ) {
                                    Icon(Icons.Rounded.CloudOff, null)
                                }
                            }
                        }
                    }
                )
            }
        ) { page ->
            when (page) {
                0 -> Navigator(MainScreen)
                else -> Text("This is page $page")
            }
        }
    }
}