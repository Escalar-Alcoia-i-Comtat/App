package org.escalaralcoiaicomtat.app.database

import kotlinx.serialization.KSerializer
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.database.Database.allByIndex

actual class DatabaseBlockingInterface actual constructor(
    objectStoreName: String,
    serializer: KSerializer<Blocking>,
    parentKey: String?,
) : BlockingInterface, DatabaseEntityInterface<Blocking>(objectStoreName, serializer, parentKey) {
    actual override suspend fun getByPathId(parentId: Long): List<Blocking> {
        return transaction { allByIndex(parentId) }
    }
}
