package network

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow

val connectivityStatus: ConnectivityStatus by lazy { ConnectivityStatus() }

expect class ConnectivityStatus() {
    val isNetworkConnected: MutableStateFlow<Boolean>

    /**
     * Locks the current thread until the connectivity status is started, or [timeout] milliseconds
     * have passed.
     *
     * @throws TimeoutCancellationException If the waiting has timed out.
     */
    suspend fun await(timeout: Long): Boolean

    fun start()

    fun stop()

    fun getStatus(success: (Boolean) -> Unit)
}

/**
 * Observes the connectivity status and updates [ConnectivityStatus.isNetworkConnected] accordingly.
 */
@Composable
fun ConnectivityStatusObserver() {
    DisposableEffect(Unit) {
        connectivityStatus.start()

        onDispose {
            connectivityStatus.stop()
        }
    }
}
