package org.escalaralcoiaicomtat.app.ui.model

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.getLongOrNullFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import org.escalaralcoiaicomtat.app.database.DatabaseInterface
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.network.AdminBackend
import org.escalaralcoiaicomtat.app.network.BasicBackend
import org.escalaralcoiaicomtat.app.network.response.data.ServerInfoResponseData
import org.escalaralcoiaicomtat.app.sync.BlockingSync
import org.escalaralcoiaicomtat.app.sync.DataSync
import org.escalaralcoiaicomtat.app.sync.SyncProcess
import org.escalaralcoiaicomtat.app.utils.IO

@OptIn(ExperimentalSettingsApi::class)
class SettingsModel : ViewModelBase() {
    private val lastDataSyncTime = settings.getLongOrNullFlow(SettingsKeys.LAST_SYNC_TIME)
        .map { epoch -> epoch?.let { Instant.fromEpochMilliseconds(it) } }
    private val lastDataSyncCause = settings.getStringOrNullFlow(SettingsKeys.LAST_SYNC_CAUSE)
        .map { cause -> cause?.let { SyncProcess.Cause.valueOf(it) } }
    val lastDataSync = combine(lastDataSyncTime, lastDataSyncCause) { time, cause ->
        if (time != null && cause != null) {
            time to cause
        } else {
            null
        }
    }

    private val lastBlockingSyncTime = settings.getLongOrNullFlow(SettingsKeys.LAST_BLOCK_SYNC_TIME)
        .map { epoch -> epoch?.let { Instant.fromEpochMilliseconds(it) } }
    private val lastBlockingSyncCause = settings.getStringOrNullFlow(SettingsKeys.LAST_BLOCK_SYNC_CAUSE)
        .map { cause -> cause?.let { SyncProcess.Cause.valueOf(it) } }
    val lastBlockingSync = combine(lastBlockingSyncTime, lastBlockingSyncCause) { time, cause ->
        if (time != null && cause != null) {
            time to cause
        } else {
            null
        }
    }

    val dataSyncStatus = DataSync.status
    val blockingSyncStatus = BlockingSync.status

    val apiKey = settings.getStringOrNullFlow(SettingsKeys.API_KEY)

    private val _serverInfo = MutableStateFlow<ServerInfoResponseData?>(null)
    val serverInfo: StateFlow<ServerInfoResponseData?> get() = _serverInfo.asStateFlow()

    private val _serverStats = MutableStateFlow<ServerStats?>(null)
    val serverStats: StateFlow<ServerStats?> get() = _serverStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    fun load() {
        launch(Dispatchers.IO) {
            val response = BasicBackend.serverInfo()
            _serverInfo.emit(response)

            _serverStats.emit(
                ServerStats(
                    DatabaseInterface.areas().count(),
                    DatabaseInterface.zones().count(),
                    DatabaseInterface.sectors().count(),
                    DatabaseInterface.paths().count(),
                )
            )
        }
    }

    private fun operate(block: suspend () -> Unit) {
        launch(Dispatchers.IO) {
            try {
                _isLoading.emit(true)
                block()
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun lock(onLock: () -> Unit) {
        operate {
            settings.remove(SettingsKeys.API_KEY)
            onLock()
        }
    }

    fun unlock(apiKey: String, onUnlock: () -> Unit) {
        operate {
            val isValid = AdminBackend.validateApiKey(apiKey)
            if (isValid) {
                settings[SettingsKeys.API_KEY] = apiKey
                onUnlock()
            }
        }
    }

    fun onIntroRequested(onNavigateToIntroRequested: () -> Unit) {
        settings.remove(SettingsKeys.SHOWN_INTRO)
        onNavigateToIntroRequested()
    }

    data class ServerStats(
        val areasCount: Int,
        val zonesCount: Int,
        val sectorsCount: Int,
        val pathsCount: Int,
    )
}
