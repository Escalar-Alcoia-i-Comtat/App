package database

import data.DataType
import database.Database.all
import database.Database.allByIndex
import database.Database.allFlow
import database.Database.delete
import database.Database.get
import database.Database.insertAll
import database.Database.transaction
import database.Database.update
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

class DatabaseDataTypeInterface<T : DataType>(
    val objectStoreName: String,
    val serializer: KSerializer<T>,
    val parentKey: String? = null,
) : DataTypeInterface<T> {
    override suspend fun insert(items: List<T>) {
        transaction(objectStoreName, true) {
            insertAll(items)
        }
    }

    override suspend fun update(items: List<T>) {
        transaction(objectStoreName) {
            for (item in items) {
                update(item)
            }
        }
    }

    override suspend fun delete(items: List<T>) {
        transaction(objectStoreName) {
            for (item in items) {
                delete(item.id)
            }
        }
    }

    override suspend fun all(): List<T> = transaction(objectStoreName) { all() }

    override fun allLive(): Flow<List<T>> = allFlow()

    override suspend fun get(id: Long): T? = transaction(objectStoreName) { get(id) }

    override suspend fun getByParentId(
        parentId: Long
    ): List<T> = transaction(objectStoreName) { allByIndex() }
}
