package ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cache.DataCache
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import sync.DataSync

class AppScreenModel: ViewModel() {
    val areas = DataCache.Areas.flow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val syncStatus = DataSync.status
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
