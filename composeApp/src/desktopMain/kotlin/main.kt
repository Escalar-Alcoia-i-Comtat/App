import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cache.StorageProvider
import cache.storageProvider
import database.DriverFactory
import database.createDatabase
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import network.ConnectivityStatus
import network.connectivityStatus

fun main() = application {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    // Initialize the database
    createDatabase(DriverFactory())

    connectivityStatus = ConnectivityStatus()

    storageProvider = StorageProvider()

    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}