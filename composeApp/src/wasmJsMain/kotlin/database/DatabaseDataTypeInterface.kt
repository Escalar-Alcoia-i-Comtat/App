package database

import data.DataType
import database.Database.TransactionContext
import database.Database.all
import database.Database.allByIndex
import database.Database.deleteAll
import database.Database.get
import database.Database.insertAll
import database.Database.newFlow
import database.Database.removeFlow
import database.Database.updateAll
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.KSerializer

class DatabaseDataTypeInterface<T : DataType>(
    val objectStoreName: String,
    val serializer: KSerializer<T>,
    val parentKey: String? = null,
) : DataTypeInterface<T> {
    fun parentKeyIndexName(): String = "${objectStoreName}.parentKey"

    private suspend fun <R> transaction(
        isWrite: Boolean = false,
        block: suspend TransactionContext<T>.() -> R
    ): R = Database.transaction(this, isWrite, block)

    private fun allFlow(): Flow<List<T>> = channelFlow {
        var closed = false
        val flowReceiver = Database.ObserverCallback {
            val all = transaction { all() }
            trySend(all).onClosed {
                // channel is closed
                closed = true
            }
        }
        newFlow(objectStoreName, flowReceiver)

        val all = transaction { all() }
        send(all)

        // Lock current thread until the flow is closed
        while (!closed) {
            delay(5)
        }

        removeFlow(objectStoreName, flowReceiver)
    }

    override suspend fun insert(items: List<T>) {
        if (items.isEmpty()) return
        transaction(true) { insertAll(items) }
    }

    override suspend fun update(items: List<T>) {
        if (items.isEmpty()) return
        transaction(true) { updateAll(items) }
    }

    override suspend fun delete(items: List<T>) {
        if (items.isEmpty()) return
        val ids = items.map { it.id }
        transaction(true) { deleteAll(ids) }
    }

    override suspend fun all(): List<T> = transaction { all() }

    override fun allLive(): Flow<List<T>> = allFlow()

    override suspend fun get(id: Long): T? = transaction { get(id) }

    override suspend fun getByParentId(parentId: Long): List<T> = transaction { allByIndex(parentId) }
}
