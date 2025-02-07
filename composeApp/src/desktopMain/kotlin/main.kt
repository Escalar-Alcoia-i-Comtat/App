import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cache.StorageProvider
import cache.storageProvider
import database.getDatabaseBuilder
import database.roomDatabaseBuilder
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import platform.Updates
import platform.backEventReceiver
import ui.state.KeyEventCollector

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()
    roomDatabaseBuilder = getDatabaseBuilder()

    CoroutineScope(Dispatchers.IO).launch {
        Updates.checkForUpdates()
    }

    Window(
        title = "Escalar Alcoià i Comtat",
        icon = painterResource(Res.drawable.icon),
        onCloseRequest = ::exitApplication,
        onPreviewKeyEvent = { KeyEventCollector.emit(it) }
    ) {
        AppRoot(
            modifier = Modifier
                .onPointerEvent(PointerEventType.Press) {
                    if (it.button == PointerButton.Back) {
                        backEventReceiver.tryEmit(true)
                    }
                }
        )
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    AppRoot()
}
