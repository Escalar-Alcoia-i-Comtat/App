package org.escalaralcoiaicomtat.app.database

import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.KSerializer
import org.escalaralcoiaicomtat.app.data.Entity
import org.escalaralcoiaicomtat.app.database.Database.all
import org.escalaralcoiaicomtat.app.database.Database.count
import org.escalaralcoiaicomtat.app.database.Database.deleteAll
import org.escalaralcoiaicomtat.app.database.Database.get
import org.escalaralcoiaicomtat.app.database.Database.insertAll
import org.escalaralcoiaicomtat.app.database.Database.updateAll

open class DatabaseEntityInterface<T : Entity>(
    val objectStoreName: String,
    val serializer: KSerializer<T>,
    val parentKey: String? = null,
) : EntityInterface<T> {
    fun parentKeyIndexName(): String = "${objectStoreName}.parentKey"

    protected suspend fun <R> transaction(
        isWrite: Boolean = false,
        block: suspend Database.TransactionContext<T>.() -> R
    ): R = Database.transaction(this, isWrite, block)

    private fun <R> observe(
        operation: suspend Database.TransactionContext<T>.() -> R
    ) = channelFlow {
        var closed = false
        val flowReceiver = Database.ObserverCallback {
            val result = transaction(block = operation)
            trySend(result).onClosed {
                // channel is closed
                closed = true
            }
        }
        Database.newFlow(objectStoreName, flowReceiver)

        val result = transaction(block = operation)
        send(result)

        // Lock current thread until the flow is closed
        while (!closed) {
            delay(5)
        }

        Database.removeFlow(objectStoreName, flowReceiver)
    }

    private fun allFlow(): Flow<List<T>> = observe { all() }

    private fun getFlow(id: Long): Flow<T?> = observe { get(id) }

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

    override suspend fun count(): Int = transaction { count() }

    override fun allLive(): Flow<List<T>> = allFlow()

    override suspend fun get(id: Long): T? = transaction { get(id) }

    override fun getLive(id: Long): Flow<T?> = getFlow(id)
}
