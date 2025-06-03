package org.escalaralcoiaicomtat.app.ui.model

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.database.DatabaseInterface
import org.escalaralcoiaicomtat.app.network.AdminBackend

class PathsScreenModel : DataScreenModel<Sector, Path>(
    childrenListAccessor = { parentId -> DatabaseInterface.paths().getByParentId(parentId) },
    parentAccessor = { id -> DatabaseInterface.sectors().get(id) }
) {
    private val _blocks = MutableStateFlow<List<Blocking>?>(null)
    val blocks: StateFlow<List<Blocking>?> get() = _blocks.asStateFlow()

    private val _editingBlocking = MutableStateFlow<Blocking?>(null)
    val editingBlocking: StateFlow<Blocking?> get() = _editingBlocking.asStateFlow()

    private val _isLoadingBlockingEdit = MutableStateFlow<Boolean>(false)
    val isLoadingBlockingEdit: StateFlow<Boolean> get() = _isLoadingBlockingEdit.asStateFlow()

    private val _previousParentId = MutableStateFlow<Long?>(null)
    val previousParentId: StateFlow<Long?> get() = _previousParentId.asStateFlow()

    private val _nextParentId = MutableStateFlow<Long?>(null)
    val nextParentId: StateFlow<Long?> get() = _nextParentId.asStateFlow()

    override suspend fun loadData(id: Long, onNotFound: () -> Unit) {
        super.loadData(id, onNotFound)

        val blocks = originalChildren.orEmpty()
            .flatMap { path ->
                DatabaseInterface.blocking().getByPathId(path.id)
            }
            .filter { it.isActive() }
        _blocks.emit(blocks)

        parent.value?.let { parent ->
            // Fetch all the child sectors of the zone of the parent sector (brothers)
            val sectors = DatabaseInterface.sectors()
                .getByParentId(parent.parentZoneId)
                .sorted()
            val sectorIdx = sectors.indexOfFirst { it.id == id }
            if (sectorIdx == 0) _previousParentId.tryEmit(null)
            else _previousParentId.tryEmit(sectors[sectorIdx - 1].id)
            if (sectorIdx +1 >= sectors.size) _nextParentId.tryEmit(null)
            else _nextParentId.tryEmit(sectors[sectorIdx + 1].id)
        }
    }

    fun editBlocking(blocking: Blocking) {
        _editingBlocking.tryEmit(blocking)
    }

    fun stopEditingBlocking() {
        _editingBlocking.tryEmit(null)
    }

    fun saveBlocking() {
        val blocking = _editingBlocking.value ?: return
        launch {
            try {
                _isLoadingBlockingEdit.emit(true)

                if (blocking.id <= 0) {
                    // It's a create operation, blocking doesn't exist
                    AdminBackend.create(blocking)
                } else {
                    // It's a patch operation, blocking already exists
                    AdminBackend.patch(blocking)
                }

                stopEditingBlocking()
            } finally {
                _isLoadingBlockingEdit.emit(false)
            }
        }
    }

    fun deleteBlocking() {
        val blocking = _editingBlocking.value ?: return
        if (blocking.id <= 0) {
            Napier.e { "Tried to delete a non-existing blocking." }
            return
        }
        launch {
            try {
                _isLoadingBlockingEdit.emit(true)

                AdminBackend.delete(blocking)

                stopEditingBlocking()
            } finally {
                _isLoadingBlockingEdit.emit(false)
            }
        }
    }
}
