package org.escalaralcoiaicomtat.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.database.getDatabaseBuilder
import org.escalaralcoiaicomtat.app.database.roomDatabaseBuilder
import org.escalaralcoiaicomtat.app.platform.Updates
import org.escalaralcoiaicomtat.app.platform.backEventReceiver
import org.escalaralcoiaicomtat.app.ui.state.KeyEventCollector
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    initializeSentry()

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
