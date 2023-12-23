package ui.model

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.ScreenModel
import database.Area
import database.Path
import database.Sector
import database.Zone
import kotlinx.coroutines.flow.MutableStateFlow
import search.Filter

class SearchModel : ScreenModel {
    val query = MutableStateFlow("")
    val isSearching = MutableStateFlow(false)

    val filteredAreas = mutableStateListOf<Area?>()
    val filteredZones = mutableStateListOf<Zone?>()
    val filteredSectors = mutableStateListOf<Sector?>()
    val filteredPaths = mutableStateListOf<Path?>()

    val filterAreas = mutableStateListOf(*Filter.Defaults)
    val filterZones = mutableStateListOf(*Filter.Defaults)
    val filterSectors = mutableStateListOf(*Filter.Defaults)
    val filterPaths = mutableStateListOf(*Filter.Defaults)
}
