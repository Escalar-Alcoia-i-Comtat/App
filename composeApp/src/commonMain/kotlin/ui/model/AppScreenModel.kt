package ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cache.DataCache
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class AppScreenModel: ViewModel() {
    val areas = DataCache.Areas.flow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val zones = DataCache.Zones.flow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val sectors = DataCache.Sectors.flow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val paths = DataCache.Paths.flow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
