import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
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
import escalaralcoiaicomtat.composeapp.generated.resources.Res
import escalaralcoiaicomtat.composeapp.generated.resources.action_skip
import escalaralcoiaicomtat.composeapp.generated.resources.action_update
import escalaralcoiaicomtat.composeapp.generated.resources.update_available_dialog_message
import escalaralcoiaicomtat.composeapp.generated.resources.update_available_dialog_message_version
import escalaralcoiaicomtat.composeapp.generated.resources.update_available_dialog_title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import network.connectivityStatus
import org.jetbrains.compose.resources.stringResource
import platform.Updates
import ui.screen.AppScreen
import ui.theme.AppTheme
import utils.createStore

val store = CoroutineScope(SupervisorJob()).createStore()

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

    AppTheme {
        val updateAvailable by Updates.updateAvailable.collectAsState()
        val latestVersion by Updates.latestVersion.collectAsState()
        if (updateAvailable) {
            AlertDialog(
                onDismissRequest = { Updates.updateAvailable.tryEmit(false) },
                title = { Text(stringResource(Res.string.update_available_dialog_title)) },
                text = {
                    Text(
                        text = latestVersion?.let {
                            stringResource(
                                Res.string.update_available_dialog_message_version,
                                BuildKonfig.VERSION_NAME,
                                it
                            )
                        } ?: stringResource(Res.string.update_available_dialog_message)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { /*TODO*/ }
                    ) { Text(stringResource(Res.string.action_update)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            settings[SettingsKeys.SKIP_VERSION] = latestVersion
                            Updates.updateAvailable.tryEmit(false)
                        }
                    ) { Text(stringResource(Res.string.action_skip)) }
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