package ui.model

import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.getLongOrNullFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import database.SettingsKeys
import database.settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import network.Backend
import utils.IO

@OptIn(ExperimentalSettingsApi::class)
class SettingsModel : ViewModelBase() {
    val lastSyncTime = settings.getLongOrNullFlow(SettingsKeys.LAST_SYNC_TIME)
    val lastSyncCause = settings.getStringOrNullFlow(SettingsKeys.LAST_SYNC_CAUSE)
    val apiKey = settings.getStringOrNullFlow(SettingsKeys.API_KEY)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    private fun operate(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
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
            val isValid = Backend.validateApiKey(apiKey)
            if (isValid) {
                settings[SettingsKeys.API_KEY] = apiKey
                onUnlock()
            }
        }
    }
}
