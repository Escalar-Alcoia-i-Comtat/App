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
import database.DriverFactory
import database.createDatabase
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.backEventReceiver

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    storageProvider = StorageProvider()

    // Initialize the database
    createDatabase(DriverFactory())

    Window(
        title = "Escalar Alcoià i Comtat",
        onCloseRequest = ::exitApplication
    ) {
        App(
            modifier = Modifier
                .onPointerEvent(PointerEventType.Press) {
                    if (it.button == PointerButton.Back) CoroutineScope(Dispatchers.IO).launch {
                        backEventReceiver.emit(true)
                    }
                }
        )
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}