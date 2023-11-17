package network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

actual class ConnectivityStatus {
    companion object {
        /**
         * How often a check should be performed.
         */
        const val CHECK_DELAY: Long = 10_000
    }

    actual val isNetworkConnected: MutableStateFlow<Boolean> = MutableStateFlow(true)

    private val isStarted = MutableStateFlow(false)

    private val client = HttpClient()

    private var loopingJob: Job? = null

    actual fun start() {
        if (loopingJob != null) return

        loopingJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val isConnected = try {
                    client.get("https://google.com")

                    true
                } catch (_: Exception) {
                    false
                }

                Napier.d(
                    "Network status: ${
                        if (isConnected) {
                            "Connected"
                        } else {
                            "Disconnected"
                        }
                    }"
                )

                isNetworkConnected.emit(isConnected)

                delay(CHECK_DELAY)
            }
        }.also {
            Napier.d("Started")
            isStarted.value = true
        }
    }

    actual fun stop() {
        isStarted.value = false
        runBlocking { loopingJob?.cancelAndJoin() }
        loopingJob = null

        Napier.d("Stopped")
    }

    actual fun getStatus(success: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            isNetworkConnected.collect { status ->
                withContext(Dispatchers.Main) {
                    success(status)
                }
            }
        }
    }

    /**
     * Locks the current thread until the connectivity status is started, or [timeout] milliseconds
     * have passed.
     *
     * @throws TimeoutCancellationException If the waiting has timed out.
     */
    actual suspend fun await(timeout: Long): Boolean {
        withTimeout(timeout) {
            while (!isStarted.value) {
                // Wait a little bit until next check
                delay(1)
            }
        }

        return isNetworkConnected.value
    }
}
