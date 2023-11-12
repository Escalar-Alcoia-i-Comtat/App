package network

import cocoapods.Reachability.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class ConnectivityStatus {
    actual val isNetworkConnected = MutableStateFlow(false)

    private var reachability: Reachability? = null

    actual fun start() {
        dispatch_async(dispatch_get_main_queue()) {
            reachability = Reachability.reachabilityForInternetConnection()

            val reachableCallback = { _: Reachability? ->
                dispatch_async(dispatch_get_main_queue()) {
                    Napier.d("Connected")

                    isNetworkConnected.value = true
                }
            }
            reachability?.reachableBlock = reachableCallback

            val unreachableCallback = { _: Reachability? ->
                dispatch_async(dispatch_get_main_queue()) {
                    Napier.d("Disconnected")

                    isNetworkConnected.value = false
                }
            }
            reachability?.unreachableBlock = unreachableCallback

            reachability?.startNotifier()

            dispatch_async(dispatch_get_main_queue()) {
                isNetworkConnected.value = reachability?.isReachable() ?: false

                Napier.d("Initial reachability: ${reachability?.isReachable()}")
            }
        }
    }

    actual fun stop() {
        reachability?.stopNotifier()
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
