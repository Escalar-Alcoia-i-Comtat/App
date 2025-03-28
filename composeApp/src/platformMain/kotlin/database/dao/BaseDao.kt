package database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import data.DataType
import database.DataTypeInterface
import database.entity.DatabaseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface BaseDao<Type: DataType, Entity: DatabaseEntity<Type>> {
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

    /**
     * Tries to get an item by its parent id.
     * May throw [UnsupportedOperationException] if the item does not have a parent.
     */
    suspend fun getByParentId(parentId: Long): List<Entity>

    fun asInterface(): DataTypeInterface<Type> = object : DataTypeInterface<Type> {
        override suspend fun insert(items: List<Type>) {
            val entities = items.map(::constructor)
            for (entity in entities) insert(entity)
        }

        override suspend fun update(items: List<Type>) {
            val entities = items.map(::constructor)
            for (entity in entities) update(entity)
        }

        override suspend fun delete(items: List<Type>) {
            val entities = items.map(::constructor)
            for (entity in entities) delete(entity)
        }

        override suspend fun all(): List<Type> = this@BaseDao.all().map { it.convert() }

        override fun allLive(): Flow<List<Type>> = this@BaseDao.allLive().map { list ->
            list.map { it.convert() }
        }

        override suspend fun get(id: Long): Type? = this@BaseDao.get(id)?.convert()

        override suspend fun getByParentId(parentId: Long): List<Type> {
            return this@BaseDao.getByParentId(parentId).map { it.convert() }
        }
    }
}
