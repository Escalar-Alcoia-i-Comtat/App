import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import cafe.adriel.voyager.navigator.Navigator
import data.EDataType
import network.connectivityStatus
import ui.screen.AppScreen

@Composable
fun App(
    initial: Pair<EDataType, Long>? = null
) {
    DisposableEffect(Unit) {
        connectivityStatus.start()

        onDispose {
            connectivityStatus.stop()
        }
    }

    MaterialTheme {
        Navigator(
            screen = AppScreen(initial)
        )
    }
}