package ui.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import database.Area
import database.Path
import database.Sector
import database.Zone
import kotlinx.coroutines.flow.MutableStateFlow
import search.Filter

class SearchModel : ViewModel() {
    val query = MutableStateFlow("")
    val isSearching = MutableStateFlow(false)

    val filteredAreas = mutableStateListOf<Area?>()
    val filteredZones = mutableStateListOf<Zone?>()
    val filteredSectors = mutableStateListOf<Sector?>()
    val filteredPaths = mutableStateListOf<Path?>()

    val filterAreas = Filter.Defaults
    val filterZones = Filter.Defaults
    val filterSectors = Filter.Defaults
    val filterPaths = Filter.Defaults

    fun dismiss() {
        isSearching.value = false
        query.value = ""
    }
}
