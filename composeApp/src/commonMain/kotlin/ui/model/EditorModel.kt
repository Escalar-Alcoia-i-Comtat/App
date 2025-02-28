package ui.model

import data.Area
import data.DataType
import data.DataTypes
import data.Path
import data.Sector
import data.Zone
import database.DatabaseInterface
import database.byType
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import network.AdminBackend

class EditorModel<DT : DataType>(val type: DataTypes<DT>, val id: Long?) : ViewModelBase() {
    private val databaseInterface = DatabaseInterface.byType(type)

    private var originalItem: DT? = null

    private val _item = MutableStateFlow<DT?>(null)
    val item get() = _item.asStateFlow()

    val isDirty = _item.map { it != originalItem }

    private val _parents = MutableStateFlow<List<DataType>?>(null)
    val parents get() = _parents.asStateFlow()

    private val _files = MutableStateFlow<Map<String, PlatformFile>>(emptyMap())
    val files get() = _files.asStateFlow()
    private val filesMutex = Semaphore(1)

    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    private val _progress = MutableStateFlow<Float?>(null)
    val progress get() = _progress.asStateFlow()

    fun load(onNotFound: () -> Unit) {
        if (id == null) {
            Napier.d { "Creating a new ${type.name}..." }
            _item.tryEmit(type.default())
            return
        }
        launch {
            Napier.d { "Editing ${type.name}#$id..." }
            val item = databaseInterface.get(id) ?: return@launch onNotFound()
            originalItem = item
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

    fun updateItem(value: DT) {
        _item.tryEmit(value)
    }

    fun setFile(key: String, file: PlatformFile?) {
        launch {
            filesMutex.withPermit {
                val files = _files.value.toMutableMap()
                if (file == null) {
                    files -= key
                } else {
                    files[key] = file
                }
                _files.tryEmit(files)
            }
        }
    }

    fun delete(onComplete: () -> Unit) {
        launch {
            try {
                _isLoading.emit(true)
                _progress.emit(0f)

                when (val item = item.value) {
                    is Area -> AdminBackend.delete(item)
                    is Zone -> AdminBackend.delete(item)
                    is Sector -> AdminBackend.delete(item)
                    is Path -> AdminBackend.delete(item)
                    else -> {
                        Napier.w { "Tried to delete an unknown data type." }
                        return@launch
                    }
                }

                onComplete()
            } finally {
                _isLoading.emit(false)
                _progress.emit(null)
            }
        }
    }

    fun save() {
        launch {
            try {
                _isLoading.emit(true)
                _progress.emit(0f)

                val image = filesMutex.withPermit { _files.value[FILE_KEY_IMAGE] }
                val kmz = filesMutex.withPermit { _files.value[FILE_KEY_KMZ] }
                val gpx = filesMutex.withPermit { _files.value[FILE_KEY_GPX] }

                val progress: suspend (current: Long, total: Long) -> Unit = { current, total ->
                    val progress = current.toDouble() / total
                    _progress.emit(progress.toFloat())
                }

                val modifiedItem = when (val item = item.value) {
                    is Area -> AdminBackend.patch(item, image, progress)
                    is Zone -> AdminBackend.patch(item, image, kmz, progress)
                    is Sector -> AdminBackend.patch(item, image, gpx, progress)
                    is Path -> AdminBackend.patch(item, progress)
                    else -> {
                        Napier.w { "Tried to save an unknown data type." }
                        return@launch
                    }
                }
                @Suppress("UNCHECKED_CAST")
                modifiedItem as DT?
                if (modifiedItem != null) {
                    originalItem = modifiedItem
                    _item.emit(modifiedItem)
                    _files.emit(emptyMap())
                }
            } finally {
                _isLoading.emit(false)
                _progress.emit(null)
            }
        }
    }

    companion object {
        const val FILE_KEY_IMAGE = "image"
        const val FILE_KEY_KMZ = "kmz"
        const val FILE_KEY_GPX = "gpx"
    }
}
