package org.escalaralcoiaicomtat.app.database

import org.escalaralcoiaicomtat.app.data.DataType

interface DataTypeInterface<Type : DataType> : EntityInterface<Type> {
    /**
     * Tries to get an item by its parent id.
     * May throw [UnsupportedOperationException] if the item does not have a parent.
     */
    suspend fun getByParentId(parentId: Long): List<Type>
}
