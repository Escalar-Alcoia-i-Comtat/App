package org.escalaralcoiaicomtat.app.database

import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.serialization.KSerializer
import org.escalaralcoiaicomtat.app.data.Entity
import org.escalaralcoiaicomtat.app.database.Database.deleteAll
import org.escalaralcoiaicomtat.app.database.Database.insertAll
import org.escalaralcoiaicomtat.app.database.Database.updateAll

actual open class DatabaseEntityInterface<T : Entity> actual constructor(
    actual val objectStoreName: String,
    actual val serializer: KSerializer<T>,
    actual val parentKey: String?,
) : EntityInterface<T> {
    fun parentKeyIndexName(): String = "${objectStoreName}.parentKey"

    protected suspend fun <R> transaction(
        isWrite: Boolean = false,
        block: suspend Database.TransactionContext<T>.() -> R
    ): R = Database.transaction(this, isWrite, block)

    private fun <R> observe(
        operation: suspend Database.TransactionContext<T>.() -> R
    ) = channelFlow {
        // TODO: Test that this works correctly
        val mutex = Semaphore(1, 1)
        val flowReceiver = Database.ObserverCallback {
            val result = transaction(block = operation)
            trySend(result).onClosed {
                // channel is closed
                mutex.release()
            }
        }
        Database.newFlow(objectStoreName, flowReceiver)

        val result = transaction(block = operation)
        send(result)

        // Lock current thread until the flow is closed
        mutex.acquire()

        Database.removeFlow(objectStoreName, flowReceiver)
    }

    private fun allFlow(): Flow<List<T>> = observe { all() }

    private fun getFlow(id: Long): Flow<T?> = observe { get(id) }

    actual override suspend fun insert(items: List<T>) {
        if (items.isEmpty()) return
        transaction(true) { insertAll(items) }
    }

    actual override suspend fun update(items: List<T>) {
        if (items.isEmpty()) return
        transaction(true) { updateAll(items) }
    }

    actual override suspend fun delete(items: List<T>) {
        if (items.isEmpty()) return
        val ids = items.map { it.id }
        transaction(true) { deleteAll(ids) }
    }

    actual override suspend fun all(): List<T> = transaction { all() }

    actual override suspend fun count(): Int = transaction { count() }

    actual override fun allLive(): Flow<List<T>> = allFlow()

    actual override suspend fun get(id: Long): T? = transaction { get(id) }

    actual override fun getLive(id: Long): Flow<T?> = getFlow(id)
}
