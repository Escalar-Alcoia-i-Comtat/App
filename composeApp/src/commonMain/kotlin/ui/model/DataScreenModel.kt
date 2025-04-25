package ui.model

import androidx.lifecycle.viewModelScope
import data.DataType
import data.DataTypeWithImage
import data.DataTypeWithParent
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.IO

abstract class DataScreenModel<Parent : DataTypeWithImage, Children : DataTypeWithParent>(
    protected val childrenListAccessor: suspend (parentId: Long) -> List<Children>,
    private val parentListAccessor: suspend (id: Long) -> Parent?
) : ViewModelBase() {
    /**
     * Whether the children should be ordered automatically upon fetch.
     * [DataType.compareTo] is used for sorting.
     */
    open val sortChildren: Boolean = true

    private val _parent = MutableStateFlow<Parent?>(null)
    val parent: StateFlow<Parent?> get() = _parent.asStateFlow()

    protected var originalChildren: List<Children>? = null
        private set
    protected val _children = MutableStateFlow<List<Children>?>(null)
    val children: StateFlow<List<Children>?> get() = _children.asStateFlow()

    /**
     * When not `null`, displays a bottom sheet with the contents desired.
     */
    val displayingChild: StateFlow<Children?> get() = _displayingChild.asStateFlow()
    private val _displayingChild = MutableStateFlow<Children?>(null)

    fun load(id: Long, onNotFound: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        originalChildren = childrenListAccessor(id)
        _children.emit(
            if (sortChildren) originalChildren!!.sorted()
            else originalChildren
        )

        val dbParent = parentListAccessor(id)
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
