package ui.model

import database.DatabaseInterface

class AppScreenModel : ViewModelBase() {
    val areas = DatabaseInterface.areas().allLive() // .stateIn(null)
    val zones = DatabaseInterface.zones().allLive() // .stateIn(null)
    val sectors = DatabaseInterface.sectors().allLive() // .stateIn(null)
    val paths = DatabaseInterface.paths().allLive() // .stateIn(null)
}
