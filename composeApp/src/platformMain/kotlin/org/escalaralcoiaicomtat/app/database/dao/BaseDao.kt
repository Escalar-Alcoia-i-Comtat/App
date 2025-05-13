package org.escalaralcoiaicomtat.app.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.escalaralcoiaicomtat.app.database.EntityInterface
import org.escalaralcoiaicomtat.app.database.entity.DatabaseEntity
import org.escalaralcoiaicomtat.app.data.Entity as AppEntity

interface BaseDao<Type: AppEntity, Entity: DatabaseEntity<Type>> {
    fun constructor(type: Type): Entity

    @Insert
    suspend fun insert(item: Entity)

    @Update
    suspend fun update(item: Entity)

    @Delete
    suspend fun delete(item: Entity)

    suspend fun all(): List<Entity>

    fun allLive(): Flow<List<Entity>>

    suspend fun get(id: Long): Entity?

    fun asInterface(): EntityInterface<Type> = EntityInterfaceImpl(this)

    open class EntityInterfaceImpl<Type: AppEntity, Entity: DatabaseEntity<Type>>(
        protected val dao: BaseDao<Type, Entity>
    ) : EntityInterface<Type> {
        override suspend fun insert(items: List<Type>) {
            val entities = items.map(dao::constructor)
            for (entity in entities) dao.insert(entity)
        }

        override suspend fun update(items: List<Type>) {
            val entities = items.map(dao::constructor)
            for (entity in entities) dao.update(entity)
        }

        override suspend fun delete(items: List<Type>) {
            val entities = items.map(dao::constructor)
            for (entity in entities) dao.delete(entity)
        }

        override suspend fun all(): List<Type> = dao.all().map { it.convert() }

        override fun allLive(): Flow<List<Type>> = dao.allLive().map { list ->
            list.map { it.convert() }
        }

        override suspend fun get(id: Long): Type? = dao.get(id)?.convert()
    }
}
