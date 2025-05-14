package org.escalaralcoiaicomtat.app.database

import kotlinx.serialization.KSerializer
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.database.Database.allByIndex

class DatabaseBlockingInterface(
    objectStoreName: String,
    serializer: KSerializer<Blocking>,
    parentKey: String? = null,
) : BlockingInterface, DatabaseEntityInterface<Blocking>(objectStoreName, serializer, parentKey) {
    override suspend fun getByPathId(parentId: Long): List<Blocking> {
        return transaction { allByIndex(parentId) }
    }
}
