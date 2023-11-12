package network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class ConnectivityStatus(private val context: Context) {
    actual val isNetworkConnected = MutableStateFlow(false)

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

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
        } catch (e: Exception) {
            Napier.d("Failed to start: ${e.message.toString()}")
            e.printStackTrace()
            isNetworkConnected.value = false
        }
    }

    actual fun stop() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
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
}
