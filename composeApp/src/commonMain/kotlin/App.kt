import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import data.EDataType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import network.connectivityStatus
import ui.screen.AppScreen
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

    MaterialTheme {
        Navigator(
            screen = AppScreen(initial)
        ) {
            Box(modifier = Modifier.fillMaxSize().then(modifier)) {
                CurrentScreen()
            }
        }
    }
}