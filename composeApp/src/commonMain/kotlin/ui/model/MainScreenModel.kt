package ui.model

import androidx.lifecycle.ViewModel
import cache.DataCache
import kotlinx.coroutines.flow.MutableStateFlow

class MainScreenModel : ViewModel() {
    /**
     * If true, a warning will be shown notifying the user that a network connection is not
     * available, and that no data is downloaded locally, so it's needed to connect at some
     * point to fetch the data.
     */
    val showConnectionNotAvailableWarning = MutableStateFlow(false)

    val areas = DataCache.Areas.flow()
}
