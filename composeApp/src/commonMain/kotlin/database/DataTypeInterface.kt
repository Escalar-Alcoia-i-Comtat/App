package database

import data.DataType
import kotlinx.coroutines.flow.Flow

interface DataTypeInterface<Type : DataType> {
    suspend fun insert(items: List<Type>)

    suspend fun update(items: List<Type>)

    suspend fun delete(items: List<Type>)

    suspend fun all(): List<Type>

    fun allLive(): Flow<List<Type>>

    suspend fun get(id: Long): Type?

    /**
     * Tries to get an item by its parent id.
     * May throw [UnsupportedOperationException] if the item does not have a parent.
     */
    suspend fun getByParentId(parentId: Long): List<Type>

    suspend fun updateOrInsert(list: List<Type>) {
        val insert = mutableListOf<Type>()
        val update = mutableListOf<Type>()
        val delete = mutableListOf<Type>()

        val entities = all()
        for (entity in list) {
            val existing = entities.find { it.id == entity.id }
            if (existing == null) {
                insert += entity
            } else {
                update += entity
            }
        }
        for (entity in entities) {
            if (list.find { it.id == entity.id } == null) {
                delete += entity
            }
        }

        insert(insert)
        update(update)
        delete(delete)
    }
}
