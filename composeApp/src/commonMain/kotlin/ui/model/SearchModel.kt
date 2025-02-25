package ui.model

import androidx.compose.runtime.mutableStateListOf
import data.Area
import data.Path
import data.Sector
import data.Zone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import search.Filter

class SearchModel : ViewModelBase() {
    val query: StateFlow<String> get() = _query.asStateFlow()
    private val _query = MutableStateFlow("")

    val isSearching: StateFlow<Boolean> get() = _isSearching.asStateFlow()
    private val _isSearching = MutableStateFlow(false)

    val filteredAreas = mutableStateListOf<Area?>()
    val filteredZones = mutableStateListOf<Zone?>()
    val filteredSectors = mutableStateListOf<Sector?>()
    val filteredPaths = mutableStateListOf<Path?>()

    val filterAreas = Filter.Defaults
    val filterZones = Filter.Defaults
    val filterSectors = Filter.Defaults
    val filterPaths = Filter.Defaults

    /**
     * Opens the search UI.
     */
    fun search() {
        _isSearching.value = true
    }

    /**
     * Searches for the given query.
     */
    fun search(query: String) {
        _query.value = query
    }

    fun dismiss() {
        _isSearching.value = false
        _query.value = ""
    }
}
