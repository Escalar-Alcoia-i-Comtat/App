package org.escalaralcoiaicomtat.app.database

import kotlinx.serialization.KSerializer
import org.escalaralcoiaicomtat.app.data.DataType
import org.escalaralcoiaicomtat.app.database.Database.allByIndex

class DatabaseDataTypeInterface<T : DataType>(
    objectStoreName: String,
    serializer: KSerializer<T>,
    parentKey: String? = null,
) : DataTypeInterface<T>, DatabaseEntityInterface<T>(objectStoreName, serializer, parentKey) {
    override suspend fun getByParentId(parentId: Long): List<T> {
        return transaction { allByIndex(parentId) }
    }
}
