package ui.model

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.DataType
import data.DataTypeWithImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class DataScreenModel<Parent : DataTypeWithImage, Children : DataType>(
    private val parentQuery: suspend (id: Long) -> Parent?,
    private val childrenQuery: suspend (parentId: Long) -> List<Children>
) : ScreenModel {
    val parent = MutableStateFlow<Parent?>(null)
    val children = MutableStateFlow<List<Children>?>(null)

    val notFound = MutableStateFlow(false)

    fun load(id: Long) = screenModelScope.launch(Dispatchers.IO) {
        val dbChildren = childrenQuery(id)
        children.emit(dbChildren)

        val dbParent = parentQuery(id)
        if (dbParent == null) {
            notFound.emit(true)
        } else {
            parent.emit(dbParent)
        }
    }
}
