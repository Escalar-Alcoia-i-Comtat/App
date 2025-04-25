package org.escalaralcoiaicomtat.app.ui.model

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.search.Filter

class SearchModel : ViewModelBase() {
    val query: StateFlow<String> get() = _query.asStateFlow()
    private val _query = MutableStateFlow("")

    val isSearching: StateFlow<Boolean> get() = _isSearching.asStateFlow()
    private val _isSearching = MutableStateFlow(false)

    val filteredAreas = mutableStateListOf<Area?>()
    val filteredZones = mutableStateListOf<Zone?>()
    val filteredSectors = mutableStateListOf<Sector?>()
    val filteredPaths = mutableStateListOf<Path?>()

    val filterAreas = Filter.Companion.Defaults
    val filterZones = Filter.Companion.Defaults
    val filterSectors = Filter.Companion.Defaults
    val filterPaths = Filter.Companion.Defaults

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
