package ui.model

import cafe.adriel.voyager.core.model.ScreenModel
import data.DataType
import kotlinx.coroutines.flow.MutableStateFlow

class AppScreenModel : ScreenModel {
    val selection = MutableStateFlow<DataType?>(null)

    fun clear() { selection.value = null }
}
