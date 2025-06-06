package org.escalaralcoiaicomtat.app.database

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import org.escalaralcoiaicomtat.app.data.Entity

expect open class DatabaseEntityInterface<T : Entity>(
    objectStoreName: String,
    serializer: KSerializer<T>,
    parentKey: String? = null,
) : EntityInterface<T> {
    val objectStoreName: String
    val serializer: KSerializer<T>
    val parentKey: String?

    override suspend fun insert(items: List<T>)

    override suspend fun update(items: List<T>)

    override suspend fun delete(items: List<T>)

    override suspend fun all(): List<T>

    override suspend fun count(): Int

    override fun allLive(): Flow<List<T>>

    override suspend fun get(id: Long): T?

    override fun getLive(id: Long): Flow<T?>
}
