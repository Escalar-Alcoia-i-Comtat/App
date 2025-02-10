package ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.DataType
import data.DataTypes
import data.Path
import data.Sector
import data.Zone
import database.DatabaseInterface
import database.byType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.IO

class EditorModel<DT : DataType>(val type: DataTypes<DT>, val id: Long?) : ViewModel() {
    private val databaseInterface = DatabaseInterface.byType(type)

    private val _item = MutableStateFlow<DT?>(null)
    val item get() = _item.asStateFlow()

    private val _parents = MutableStateFlow<List<DataType>?>(null)
    val parents get() = _parents.asStateFlow()

    private val _imageFile = MutableStateFlow<PlatformFile?>(null)
    val imageFile get() = _imageFile.asStateFlow()

    fun load(onNotFound: () -> Unit) {
        if (id == null) {
            _item.tryEmit(type.default())
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val item = databaseInterface.get(id) ?: return@launch onNotFound()
            _item.emit(item)

            val parents = when (item) {
                is Zone -> DatabaseInterface.areas().all()
                is Sector -> DatabaseInterface.zones().all()
                is Path -> DatabaseInterface.sectors().all()
                else -> null
            }
            _parents.emit(parents)
        }
    }

    fun updateItem(value: (current: DT) -> DT) {
        val current = _item.value ?: return
        val newValue = value(current)
        _item.tryEmit(newValue)
    }

    fun setImageFile(file: PlatformFile?) {
        _imageFile.tryEmit(file)
    }
}
