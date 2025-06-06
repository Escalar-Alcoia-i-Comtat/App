package org.escalaralcoiaicomtat.app.database

import kotlinx.serialization.KSerializer
import org.escalaralcoiaicomtat.app.data.DataType

expect class DatabaseDataTypeInterface<T : DataType>(
    objectStoreName: String,
    serializer: KSerializer<T>,
    parentKey: String? = null,
) : DataTypeInterface<T>, DatabaseEntityInterface<T> {
    override suspend fun getByParentId(parentId: Long): List<T>
}
