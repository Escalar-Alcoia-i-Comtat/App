package org.escalaralcoiaicomtat.app.ui.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.database.DatabaseInterface

class PathsScreenModel : DataScreenModel<Sector, Path>(
    childrenListAccessor = { parentId -> DatabaseInterface.paths().getByParentId(parentId) },
    parentListAccessor = { id -> DatabaseInterface.sectors().get(id) }
) {
    private val _blocks = MutableStateFlow<List<Blocking>?>(null)
    val blocks: StateFlow<List<Blocking>?> get() = _blocks.asStateFlow()

    override suspend fun loadData(id: Long, onNotFound: () -> Unit) {
        super.loadData(id, onNotFound)

        val blocks = originalChildren.orEmpty().flatMap { path ->
            DatabaseInterface.blocking().getByPathId(path.id)
        }
        _blocks.emit(blocks)
    }
}
