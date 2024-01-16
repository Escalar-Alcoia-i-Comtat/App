import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import build.BuildKonfig
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.russhwolf.settings.set
import data.EDataType
import database.SettingsKeys
import database.settings
import dev.icerock.moko.resources.compose.stringResource
import io.github.alexzhirkevich.cupertino.AlertActionStyle
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveAlertDialog
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveTheme
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import io.github.alexzhirkevich.cupertino.adaptive.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import network.connectivityStatus
import platform.PlatformInfo
import platform.Updates
import resources.MR
import ui.screen.AppScreen
import utils.createStore

val store = CoroutineScope(SupervisorJob()).createStore()

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun App(
    initial: Pair<EDataType, Long>? = null,
    modifier: Modifier = Modifier
) {
    DisposableEffect(Unit) {
        connectivityStatus.start()

        onDispose {
            connectivityStatus.stop()
        }
    }

    AdaptiveTheme(
        PlatformInfo.type.theme
    ) {
        val updateAvailable by Updates.updateAvailable.collectAsState()
        val latestVersion by Updates.latestVersion.collectAsState()
        if (updateAvailable) {
            AdaptiveAlertDialog(
                onDismissRequest = { Updates.updateAvailable.tryEmit(false) },
                title = { Text(stringResource(MR.strings.update_available_dialog_title)) },
                message = {
                    Text(
                        text = latestVersion?.let {
                            stringResource(
                                MR.strings.update_available_dialog_message_version,
                                BuildKonfig.VERSION_NAME,
                                it
                            )
                        } ?: stringResource(MR.strings.update_available_dialog_message)
                    )
                },
                buttons = {
                    action(
                        onClick = {
                            settings[SettingsKeys.SKIP_VERSION] = latestVersion
                            Updates.updateAvailable.tryEmit(false)
                        },
                        style = AlertActionStyle.Destructive
                    ) {
                        Text(stringResource(MR.strings.action_skip))
                    }
                    action(
                        onClick = { /*TODO*/ },
                        style = AlertActionStyle.Default
                    ) {
                        Text(stringResource(MR.strings.action_update))
                    }
                }
            )
        }

        Navigator(
            screen = AppScreen(initial)
        ) {
            Box(modifier = Modifier.fillMaxSize().then(modifier)) {
                CurrentScreen()
            }
        }
    }
}