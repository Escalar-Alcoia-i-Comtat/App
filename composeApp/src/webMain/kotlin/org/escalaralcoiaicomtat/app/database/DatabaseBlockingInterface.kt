package org.escalaralcoiaicomtat.app.database

import kotlinx.serialization.KSerializer
import org.escalaralcoiaicomtat.app.data.Blocking

expect class DatabaseBlockingInterface(
    objectStoreName: String,
    serializer: KSerializer<Blocking>,
    parentKey: String? = null,
) : BlockingInterface, DatabaseEntityInterface<Blocking> {
    override suspend fun getByPathId(parentId: Long): List<Blocking>
}
