package ui.model

import androidx.lifecycle.ViewModel
import sync.DataSync
import sync.SyncProcess.Status

class AppScreenModel : ViewModel() {
    val areas = DataSync.areas.stateIn(this, emptyList())
    val zones = DataSync.zones.stateIn(this, emptyList())
    val sectors = DataSync.sectors.stateIn(this, emptyList())
    val paths = DataSync.paths.stateIn(this, emptyList())

    val syncStatus = DataSync.status.stateIn(this, Status.WAITING)
}
