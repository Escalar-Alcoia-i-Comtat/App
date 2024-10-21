package network

import kotlinx.browser.window
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withTimeout
import org.w3c.dom.events.Event

actual class ConnectivityStatus actual constructor() {
    actual val isNetworkConnected: MutableStateFlow<Boolean> = MutableStateFlow(window.navigator.onLine)

    private val onlineCallback: (Event) -> Unit = { isNetworkConnected.tryEmit(true) }
    private val offlineCallback: (Event) -> Unit = { isNetworkConnected.tryEmit(false) }

    /**
     * Locks the current thread until the connectivity status is started, or [timeout] milliseconds
     * have passed.
     *
     * @throws TimeoutCancellationException If the waiting has timed out.
     */
    actual suspend fun await(timeout: Long): Boolean {
        withTimeout(timeout) {
            while (!isNetworkConnected.value) {
                // Wait until the network is connected
            }
        }
        return isNetworkConnected.value
    }

    actual fun start() {
        window.addEventListener("online", onlineCallback)
        window.addEventListener("offline", offlineCallback)
    }

    actual fun stop() {
        window.removeEventListener("online", onlineCallback)
        window.removeEventListener("offline", offlineCallback)
    }
}