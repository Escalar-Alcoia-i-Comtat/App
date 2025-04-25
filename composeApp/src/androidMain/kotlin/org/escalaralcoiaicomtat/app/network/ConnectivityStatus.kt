package org.escalaralcoiaicomtat.app.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import io.github.aakira.napier.Napier
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withTimeout
import org.escalaralcoiaicomtat.android.applicationContext

actual class ConnectivityStatus {
    actual val isNetworkConnected = MutableStateFlow(false)

    private val connectivityManager: ConnectivityManager by lazy {
        applicationContext.getSystemService(ConnectivityManager::class.java)
    }

    private val isStarted = MutableStateFlow(false)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Napier.d("Network available")
            isNetworkConnected.value = true
        }

        override fun onLost(network: Network) {
            Napier.d("Network lost")
            isNetworkConnected.value = false
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)

            val isConnected =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

            Napier.d("Network status: ${if(isConnected){ "Connected" } else { "Disconnected" }}")

            isNetworkConnected.value = isConnected
        }
    }

    actual fun start() {
        try {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)

            val currentNetwork = connectivityManager.activeNetwork
            if(currentNetwork == null) {
                isNetworkConnected.value = false

                Napier.d("Disconnected")
            }

            Napier.d("Started")
            isStarted.value = true
        } catch (e: Exception) {
            Napier.d("Failed to start: ${e.message.toString()}")
            e.printStackTrace()
            isNetworkConnected.value = false
        }
    }

    actual fun stop() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        Napier.d("Stopped")
        isStarted.value = false
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
