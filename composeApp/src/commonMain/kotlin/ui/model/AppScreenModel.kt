package ui.model

import androidx.lifecycle.ViewModel
import sync.DataSync

class AppScreenModel : ViewModel() {
    val areas = DataSync.areas

    val syncStatus = DataSync.status
}
