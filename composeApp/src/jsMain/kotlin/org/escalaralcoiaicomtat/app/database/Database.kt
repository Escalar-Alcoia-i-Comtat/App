package org.escalaralcoiaicomtat.app.database

import io.github.aakira.napier.Napier
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.builtins.ListSerializer
import org.escalaralcoiaicomtat.app.data.Entity
import org.escalaralcoiaicomtat.app.database.indexeddb.IDBDatabase
import org.escalaralcoiaicomtat.app.database.indexeddb.IDBKey
import org.escalaralcoiaicomtat.app.database.indexeddb.IDBObjectStore
import org.escalaralcoiaicomtat.app.database.indexeddb.IDBObjectStoreOptions
import org.escalaralcoiaicomtat.app.database.indexeddb.IDBRequest
import org.escalaralcoiaicomtat.app.database.indexeddb.getDatabase
import org.escalaralcoiaicomtat.app.database.indexeddb.indexedDB
import org.escalaralcoiaicomtat.app.json
import kotlin.js.Promise

actual object Database {
    private lateinit var db: IDBDatabase

    actual fun open() {
        Promise<Any?> { resolve, reject ->
            val indexedDB = indexedDB
            if (indexedDB == null) {
                reject(UnsupportedOperationException("IndexedDB not supported"))
                return@Promise
            }
            Napier.i { "Opening database $DATABASE_NAME @ $DATABASE_VERSION..." }
            val openRequest = indexedDB.open(DATABASE_NAME, DATABASE_VERSION)
            openRequest.onupgradeneeded = { event ->
                val db = getDatabase(event)
                Napier.w { "Upgrade is needed. Old: ${event.oldVersion}, New: ${event.newVersion}" }
                if (event.oldVersion == 0) {
                    // Create the store
                    Napier.i { "Initializing database..." }
                    createObjectStores(db)
                } else if (event.oldVersion < DATABASE_VERSION) {
                    // Delete the store, and create it again
                    Napier.i { "Database needs upgrade, deleting and creating again..." }
                    indexedDB.deleteDatabase(DATABASE_NAME)
                    createObjectStores(db)
                }
            }
            openRequest.onerror = {
                Napier.e { "Could not open database: ${openRequest.error}" }
                reject(IllegalStateException("Could not open database: ${openRequest.error}"))
            }
            openRequest.onsuccess = {
                Napier.i { "Database opened successfully" }
                db = openRequest.result
                resolve(null)
            }
        }
    }

    private suspend fun await() {
        while (!this::db.isInitialized) {
            delay(5)
        }
    }

    private fun createObjectStores(database: IDBDatabase) {
        for (int in interfaces) createObjectStore(database, int)
    }

    private fun <T : Entity> createObjectStore(
        database: IDBDatabase,
        int: DatabaseEntityInterface<T>
    ) {
        Napier.d { "Creating Object Store for ${int.objectStoreName}" }
        try {
            val objectStore = database.createObjectStore(
                int.objectStoreName,
                IDBObjectStoreOptions(keyPath = "id")
            )
            if (int.parentKey != null) {
                objectStore.createIndex(int.parentKeyIndexName(), int.parentKey)
            }
        } catch (e: Exception) {
            Napier.e(e) { "Could not create object store." }
        }
    }

    suspend fun <R, T : Entity> transaction(
        ddti: DatabaseEntityInterface<T>,
        isWrite: Boolean = false,
        block: suspend TransactionContext<T>.() -> R
    ): R {
        await()

        val transaction =
            db.transaction(ddti.objectStoreName, if (isWrite) "readwrite" else "readonly")

        var error: Any? = null
        var isComplete = false

        transaction.onerror = { error = it }

        val objectStore = transaction.objectStore(ddti.objectStoreName)
        val context = TransactionContext(objectStore, ddti)
        val result = block(context)

        transaction.oncomplete = { isComplete = true }

        // Wait until an error occurs or the transaction completes
        while (!isComplete || error != null) delay(5)

        // If an error happened, throw it
        if (error != null) throw Exception(error.toString())

        // If the transaction was a write, notify all listeners
        if (isWrite) notifyUpdate(ddti.objectStoreName)

        // Finally, return the result of the block
        return result
    }

    private val flowsList: MutableMap<String, List<ObserverCallback>> = mutableMapOf()
    private val flowsListMutex = Semaphore(1)

    suspend fun newFlow(
        storeName: String,
        producer: ObserverCallback
    ) = flowsListMutex.withPermit {
        val list = flowsList[storeName].orEmpty().toMutableList()
        list += producer
        flowsList[storeName] = list
    }

    suspend fun removeFlow(
        storeName: String,
        producer: ObserverCallback
    ) = flowsListMutex.withPermit {
        val list = flowsList[storeName].orEmpty().toMutableList()
        list -= producer
        flowsList[storeName] = list
    }

    private suspend fun notifyUpdate(storeName: String) = flowsListMutex.withPermit {
        val list = flowsList[storeName] ?: return@withPermit
        for (item in list) item()
    }

    class TransactionContext<T : Entity>(
        val store: IDBObjectStore,
        val ddti: DatabaseEntityInterface<T>
    )

    private suspend fun <R : Any?> IDBObjectStore.request(
        operation: IDBObjectStore.() -> IDBRequest<R>
    ): String? = Promise { resolve, reject ->
        val req = operation(this)
        req.onsuccess = {
            val result = req.result?.let { jsonStringify(it) }
            resolve(result)
        }
        req.onerror = { reject(req.error!!) }
    }.await()

    private fun <R, I> IDBObjectStore.requestBatch(
        list: List<I>,
        onSuccess: (R) -> Unit = {},
        operation: IDBObjectStore.(I) -> IDBRequest<R>
    ) {
        for (item in list) {
            val req = operation(item)
            req.onsuccess = {
                onSuccess(req.result)
            }
            req.onerror = {
                throw req.error!!
            }
        }
    }

    suspend fun <T : Entity> TransactionContext<T>.get(id: Long): T? {
        val jsonObject = store.request {
            val key = IDBKey(id.toInt())
            store.get(key)
        } ?: return null

        return json.decodeFromString(ddti.serializer, jsonObject.toString())
    }

    fun <T : Entity> TransactionContext<T>.insertAll(data: List<T>) {
        store.requestBatch(data) { item ->
            val jsonString = json.encodeToString(ddti.serializer, item)
            val obj = jsonParse(jsonString)
            add(obj)
        }
    }

    fun <T : Entity> TransactionContext<T>.updateAll(data: List<T>) {
        store.requestBatch(data) { item ->
            val jsonString = json.encodeToString(ddti.serializer, item)
            val obj = jsonParse(jsonString)
            put(obj)
        }
    }

    fun <T : Entity> TransactionContext<T>.deleteAll(ids: List<Long>) {
        store.requestBatch(ids) { id ->
            val key = IDBKey(id.toInt())
            delete(key)
        }
    }

    suspend fun <T : Entity> TransactionContext<T>.all(): List<T> {
        val jsonArray = store.request { getAll() }?.toString() ?: "[]"
        return json.decodeFromString(ListSerializer(ddti.serializer), jsonArray)
    }

    suspend fun <T : Entity> TransactionContext<T>.count(): Int {
        val number = store.request { count() }?.toString() ?: "0"
        return number.toInt()
    }

    suspend fun <T : Entity> TransactionContext<T>.allByIndex(parentId: Long): List<T> {
        val key = IDBKey(parentId.toInt())
        val jsonArray =
            store.request { index(ddti.parentKeyIndexName()).getAll(key) }?.toString() ?: "[]"
        return json.decodeFromString(ListSerializer(ddti.serializer), jsonArray)
    }

    fun interface ObserverCallback {
        suspend operator fun invoke()
    }
}
