package org.escalaralcoiaicomtat.app.ui.model

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.getLongOrNullFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.network.AdminBackend
import org.escalaralcoiaicomtat.app.network.BasicBackend
import org.escalaralcoiaicomtat.app.network.response.data.ServerInfoResponseData
import org.escalaralcoiaicomtat.app.sync.DataSync
import org.escalaralcoiaicomtat.app.utils.IO

@OptIn(ExperimentalSettingsApi::class)
class SettingsModel : ViewModelBase() {
    val lastSyncTime = settings.getLongOrNullFlow(SettingsKeys.LAST_SYNC_TIME)
    val lastSyncCause = settings.getStringOrNullFlow(SettingsKeys.LAST_SYNC_CAUSE)
    val syncStatus = DataSync.status

    val apiKey = settings.getStringOrNullFlow(SettingsKeys.API_KEY)

    private val _serverInfo = MutableStateFlow<ServerInfoResponseData?>(null)
    val serverInfo: StateFlow<ServerInfoResponseData?> get() = _serverInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    fun load() {
        launch(Dispatchers.IO) {
            val response = BasicBackend.serverInfo()
            _serverInfo.emit(response)
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
}
