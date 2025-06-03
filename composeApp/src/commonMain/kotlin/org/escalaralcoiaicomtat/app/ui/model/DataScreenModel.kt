package org.escalaralcoiaicomtat.app.ui.model

import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.escalaralcoiaicomtat.app.data.DataType
import org.escalaralcoiaicomtat.app.data.DataTypeWithImage
import org.escalaralcoiaicomtat.app.data.DataTypeWithParent

abstract class DataScreenModel<Parent : DataTypeWithImage, Children : DataTypeWithParent>(
    protected val childrenListAccessor: suspend (parentId: Long) -> List<Children>,
    private val parentAccessor: suspend (id: Long) -> Parent?,
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

    fun load(id: Long, onNotFound: () -> Unit) = launch {
        loadData(id, onNotFound)
    }

    protected open suspend fun loadData(id: Long, onNotFound: () -> Unit) {
        originalChildren = childrenListAccessor(id)
        _children.emit(
            if (sortChildren) originalChildren!!.sorted()
            else originalChildren
        )

        val dbParent = parentAccessor(id)
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
