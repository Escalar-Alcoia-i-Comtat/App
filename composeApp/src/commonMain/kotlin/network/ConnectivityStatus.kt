package network

import kotlinx.coroutines.flow.MutableStateFlow

lateinit var connectivityStatus: ConnectivityStatus

expect class ConnectivityStatus {
    val isNetworkConnected: MutableStateFlow<Boolean>
    fun start()
    fun stop()
    fun getStatus(success: (Boolean) -> Unit)
}
