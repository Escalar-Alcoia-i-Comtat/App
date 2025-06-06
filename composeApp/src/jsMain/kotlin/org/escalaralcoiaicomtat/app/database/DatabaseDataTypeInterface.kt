package org.escalaralcoiaicomtat.app.database

import kotlinx.serialization.KSerializer
import org.escalaralcoiaicomtat.app.data.DataType
import org.escalaralcoiaicomtat.app.database.Database.allByIndex

actual class DatabaseDataTypeInterface<T : DataType> actual constructor(
    objectStoreName: String,
    serializer: KSerializer<T>,
    parentKey: String?,
) : DataTypeInterface<T>, DatabaseEntityInterface<T>(objectStoreName, serializer, parentKey) {
    actual override suspend fun getByParentId(parentId: Long): List<T> {
        return transaction { allByIndex(parentId) }
    }
}
