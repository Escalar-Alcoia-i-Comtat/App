package ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.DataType
import data.DataTypeWithImage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class DataScreenModel<Parent : DataTypeWithImage, Children : DataType>(
    private val parentQuery: suspend (id: Long) -> Parent?,
    private val childrenQuery: suspend (parentId: Long) -> List<Children>
) : ViewModel() {
    /**
     * Whether the children should be ordered automatically upon fetch.
     * [DataType.compareTo] is used for sorting.
     */
    open val sortChildren: Boolean = true

    val parent = MutableStateFlow<Parent?>(null)
    val children = MutableStateFlow<List<Children>?>(null)

    /**
     * When not `null`, displays a bottom sheet with the contents desired.
     */
    val displayingChild = MutableStateFlow<Children?>(null)

    fun load(id: Long, onNotFound: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val dbChildren = childrenQuery(id)
        children.emit(
            if (sortChildren) dbChildren.sorted()
            else dbChildren
        )

        val dbParent = parentQuery(id)
        if (dbParent == null) {
            Napier.w { "Could not find #$id" }
            withContext(Dispatchers.Main) { onNotFound() }
        } else {
            Napier.d { "Emitting #$id" }
            parent.emit(dbParent)
        }
    }
}
