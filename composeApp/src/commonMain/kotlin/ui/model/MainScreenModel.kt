package ui.model

import app.cash.sqldelight.coroutines.asFlow
import cafe.adriel.voyager.core.model.ScreenModel
import database.database
import kotlinx.coroutines.flow.MutableStateFlow

class MainScreenModel : ScreenModel {
    /**
     * If true, a warning will be shown notifying the user that a network connection is not
     * available, and that no data is downloaded locally, so it's needed to connect at some
     * point to fetch the data.
     */
    val showConnectionNotAvailableWarning = MutableStateFlow(false)

    val areas = database.areaQueries.getAll().asFlow()
}
