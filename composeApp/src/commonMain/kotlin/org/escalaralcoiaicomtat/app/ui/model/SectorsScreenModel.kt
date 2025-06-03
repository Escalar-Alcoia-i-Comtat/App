package org.escalaralcoiaicomtat.app.ui.model

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.database.DatabaseInterface
import org.escalaralcoiaicomtat.app.network.AdminBackend

class SectorsScreenModel : DataScreenModel<Zone, Sector>(
    childrenListAccessor = { parentId -> DatabaseInterface.sectors().getByParentId(parentId) },
    parentAccessor = { id -> DatabaseInterface.zones().get(id) }
) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    @OptIn(ExperimentalStdlibApi::class)
    fun moveItem(fromIndex: Int, toIndex: Int) {
        var children = children.value?.toMutableList() ?: return
        // Move the item
        val item = children.removeAt(fromIndex)
        children.add(toIndex, item)
        // Update the weights
        children = children.mapIndexed { i, sector -> sector.copy(weight = i.toString(16)) }.toMutableList()
        // Update the UI
        _children.tryEmit(children)
        // TODO: Actually send the update to the server
    }

    /**
     * Sends the updated moved items into the server.
     */
    fun saveMovedItems() {
        val children = children.value ?: return
        val originalChildren = originalChildren ?: return
        // Do not send update if children were not modified
        if (children == originalChildren) return

        launch {
            try {
                _isLoading.emit(true)

                Napier.i { "Updating ${children.size} sectors..." }
                for (child in children) {
                    Napier.d { "Updating sector ${child.id}..." }
                    AdminBackend.patch(child, null, null)
                }
                Napier.i { "All children updated" }
            } finally {
                _isLoading.emit(false)
            }
        }
    }
}
