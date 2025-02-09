package ui.model

import androidx.lifecycle.viewModelScope
import database.DatabaseInterface
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sync.DataSync
import utils.IO

class AppScreenModel : ViewModelBase() {
    val areas = DatabaseInterface.areas().allLive().stateIn(null)
    val zones = DatabaseInterface.zones().allLive().stateIn(null)
    val sectors = DatabaseInterface.sectors().allLive().stateIn(null)
    val paths = DatabaseInterface.paths().allLive().stateIn(null)

    val syncStatus = DataSync.status

    init {
        viewModelScope.launch(Dispatchers.IO) {
            syncStatus.collect {
                Napier.i { "Sync progress from model: $it" }
            }
        }
    }
}
