package org.escalaralcoiaicomtat.app.ui.model

import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.data.DataType
import org.escalaralcoiaicomtat.app.data.DataTypeWithParent
import org.escalaralcoiaicomtat.app.data.DataTypes
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.database.DatabaseInterface
import org.escalaralcoiaicomtat.app.database.byType
import org.escalaralcoiaicomtat.app.network.AdminBackend

class EditorModel<DT : DataType>(
    val type: DataTypes<DT>,
    id: Long?,
    private val parentId: Long?,
) : ViewModelBase() {
    private val databaseInterface = DatabaseInterface.byType(type)

    var id: Long? = id
        private set

    private var originalItem: DT? = null

    private val _item = MutableStateFlow<DT?>(null)
    val item get() = _item.asStateFlow()

    private val _parents = MutableStateFlow<List<DataType>?>(null)
    val parents get() = _parents.asStateFlow()

    private val _files = MutableStateFlow<Map<String, PlatformFile>>(emptyMap())
    val files get() = _files.asStateFlow()
    private val filesMutex = Semaphore(1)

    private val _modifiedFiles = MutableStateFlow<Set<String>>(emptySet())

    val isDirty: Flow<Boolean> = combine(item, _modifiedFiles) { item, modifiedFiles ->
        item != originalItem || modifiedFiles.isNotEmpty()
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    private val _progress = MutableStateFlow<Float?>(null)
    val progress get() = _progress.asStateFlow()

    private val _error = MutableStateFlow<Exception?>(null)
    val error get() = _error.asStateFlow()

    fun load(onNotFound: () -> Unit) {
        val id = id
        launch {
            val item = if (id == null) {
                Napier.d { "Creating a new ${type.name}..." }
                type.default()
            } else {
                Napier.d { "Editing ${type.name}#$id..." }
                databaseInterface.get(id)?.also { originalItem = it } ?: return@launch run {
                    Napier.e { "Could not find ${type.name} with id $id" }
                    onNotFound()
                }
            }
            _item.emit(item)

            if (type.parentDataType != null) {
                val pdbi = DatabaseInterface.byType(type.parentDataType)
                val parents = pdbi.all()
                Napier.d { "Got ${parents.size} parents." }
                _parents.tryEmit(parents)

                if (item is DataTypeWithParent && parentId != null) {
                    // Make sure the parent exists
                    pdbi.get(parentId)?.let {
                        @Suppress("UNCHECKED_CAST")
                        _item.emit(
                            item.copy(parentId = parentId) as DT
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _error.tryEmit(null)
    }

    fun updateItem(value: DT) {
        _item.tryEmit(value)
    }

    fun setFile(key: String, file: PlatformFile?) {
        launch {
            filesMutex.withPermit {
                val files = _files.value.toMutableMap()
                val modifiedFiles = _modifiedFiles.value.toMutableSet()
                if (file == null) {
                    files -= key
                    // Unmark the file key as modified
                    modifiedFiles -= key
                } else {
                    files[key] = file
                    // Mark the file key as modified
                    modifiedFiles += key
                }
                _files.tryEmit(files)
                _modifiedFiles.tryEmit(modifiedFiles)
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
            } catch (e: Exception) {
                Napier.e(e) { "Could not save." }
                _error.emit(e)
            } finally {
                _isLoading.emit(false)
                _progress.emit(null)
            }
        }
    }

    fun save(onComplete: () -> Unit) {
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

                if (id == null) {
                    // Create operation
                    val newItem = when (val item = item.value) {
                        is Area -> AdminBackend.create(item, image, progress)
                        is Zone -> AdminBackend.create(item, image, kmz, progress)
                        is Sector -> AdminBackend.create(item, image, gpx, progress)
                        is Path -> AdminBackend.create(item, progress)
                        else -> {
                            Napier.w { "Tried to create an unknown data type." }
                            return@launch
                        }
                    }
                    @Suppress("UNCHECKED_CAST")
                    newItem as DT

                    id = newItem.id
                    originalItem = newItem
                    _item.emit(newItem)
                    _files.emit(emptyMap())

                    onComplete()
                } else {
                    // Update operation
                    val modifiedItem = when (val item = item.value) {
                        is Area -> AdminBackend.patch(item, image, progress)
                        is Zone -> AdminBackend.patch(item, image, kmz, progress)
                        is Sector -> AdminBackend.patch(item, image, gpx, progress)
                        is Path -> AdminBackend.patch(item, progress)
                        else -> {
                            Napier.w { "Tried to patch an unknown data type." }
                            return@launch
                        }
                    }
                    @Suppress("UNCHECKED_CAST")
                    modifiedItem as DT?
                    if (modifiedItem != null) {
                        originalItem = modifiedItem
                        _item.emit(modifiedItem)
                        _files.emit(emptyMap())
                        _modifiedFiles.emit(emptySet())
                    }

                    onComplete()
                }
            } catch (e: Exception) {
                Napier.e(e) { "Could not save." }
                _error.emit(e)
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
