package ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.DataType
import data.DataTypeWithImage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    val parent: StateFlow<Parent?> get() = _parent.asStateFlow()
    private val _parent = MutableStateFlow<Parent?>(null)

    val children: StateFlow<List<Children>?> get() = _children.asStateFlow()
    private val _children = MutableStateFlow<List<Children>?>(null)

    /**
     * When not `null`, displays a bottom sheet with the contents desired.
     */
    val displayingChild: StateFlow<Children?> get() = _displayingChild.asStateFlow()
    private val _displayingChild = MutableStateFlow<Children?>(null)

    fun load(id: Long, onNotFound: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val dbChildren = childrenQuery(id)
        _children.emit(
            if (sortChildren) dbChildren.sorted()
            else dbChildren
        )

        val dbParent = parentQuery(id)
        if (dbParent == null) {
            Napier.w { "Could not find #$id" }
            withContext(Dispatchers.Main) { onNotFound() }
        } else {
            Napier.d { "Emitting #$id" }
            _parent.emit(dbParent)
        }
    }

    fun selectChild(child: Children?) {
        _displayingChild.value = child
    }
}
