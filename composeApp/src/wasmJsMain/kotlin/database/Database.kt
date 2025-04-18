package database

import data.DataType
import database.indexeddb.IDBDatabase
import database.indexeddb.IDBKey
import database.indexeddb.IDBObjectStore
import database.indexeddb.IDBObjectStoreOptions
import database.indexeddb.IDBRequest
import database.indexeddb.getDatabase
import database.indexeddb.indexedDB
import exception.JavaScriptException.Companion.toJavaScriptException
import interop.DOMException
import io.github.aakira.napier.Napier
import json
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.builtins.ListSerializer
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

object Database {
    private const val DATABASE_NAME = "escalar-alcoia-i-comtat"
    private const val DATABASE_VERSION = 1

    private lateinit var db: IDBDatabase

    suspend fun open() = suspendCoroutine<Unit> { cont ->
        val indexedDB = indexedDB
        if (indexedDB == null) {
            cont.resumeWithException(UnsupportedOperationException("IndexedDB not supported"))
            return@suspendCoroutine
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
            cont.resumeWithException(
                Exception("Could not open database: ${openRequest.error}")
            )
        }
        openRequest.onsuccess = {
            Napier.i { "Database opened successfully" }
            db = openRequest.result
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

    private fun <T : DataType> createObjectStore(
        database: IDBDatabase,
        int: DatabaseDataTypeInterface<T>
    ) {
        Napier.d { "Creating Object Store for ${int.objectStoreName}" }
        try {
            val objectStore = database.createObjectStore(
                int.objectStoreName,
                IDBObjectStoreOptions(keyPath = "id".toJsString())
            )
            if (int.parentKey != null) {
                objectStore.createIndex(int.parentKeyIndexName(), int.parentKey.toJsString())
            }
        } catch (e: JsException) {
            Napier.e(e) { "Could not create object store." }
        }
    }

    suspend fun <R, T: DataType> transaction(
        ddti: DatabaseDataTypeInterface<T>,
        isWrite: Boolean = false,
        block: suspend TransactionContext<T>.() -> R
    ): R {
        await()

        val transaction = db.transaction(ddti.objectStoreName, if (isWrite) "readwrite" else "readonly")

        var error: JsAny? = null
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

    class TransactionContext<T : DataType>(
        val store: IDBObjectStore,
        val ddti: DatabaseDataTypeInterface<T>
    )

    private suspend fun <R : JsAny?> IDBObjectStore.request(
        operation: IDBObjectStore.() -> IDBRequest<R>
    ): JsString? = Promise { resolve, reject ->
        val req = operation(this)
        req.onsuccess = {
            val result = req.result?.let { jsonStringify(it) }
            resolve(result)
        }
        req.onerror = { reject(req.error!!) }
    }.await()

    private fun <R : JsAny?, I> IDBObjectStore.requestBatch(
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
                throw req.error!!.unsafeCast<DOMException>().toJavaScriptException()
            }
        }
    }

    suspend fun <T: DataType> TransactionContext<T>.get(id: Long): T? {
        val jsonObject = store.request {
            val key = IDBKey(id.toInt())
            store.get(key)
        } ?: return null

        return json.decodeFromString(ddti.serializer, jsonObject.toString())
    }

    fun <T : DataType> TransactionContext<T>.insertAll(data: List<T>) {
        store.requestBatch(data) { item ->
            val jsonString = json.encodeToString(ddti.serializer, item)
            val obj: JsAny = jsonParse(jsonString)
            add(obj)
        }
    }

    fun <T : DataType> TransactionContext<T>.updateAll(data: List<T>) {
        store.requestBatch(data) { item ->
            val jsonString = json.encodeToString(ddti.serializer, item)
            val obj: JsAny = jsonParse(jsonString)
            put(obj)
        }
    }

    fun <T : DataType> TransactionContext<T>.deleteAll(ids: List<Long>) {
        store.requestBatch(ids) { id ->
            val key = IDBKey(id.toInt())
            delete(key)
        }
    }

    suspend fun <T : DataType> TransactionContext<T>.all(): List<T> {
        val jsonArray = store.request { getAll() }?.toString() ?: "[]"
        return json.decodeFromString(ListSerializer(ddti.serializer), jsonArray)
    }

    suspend fun <T : DataType> TransactionContext<T>.allByIndex(parentId: Long): List<T> {
        val key = IDBKey(parentId.toInt())
        val jsonArray = store.request { index(ddti.parentKeyIndexName()).getAll(key) }?.toString() ?: "[]"
        return json.decodeFromString(ListSerializer(ddti.serializer), jsonArray)
    }

    fun interface ObserverCallback {
        suspend operator fun invoke()
    }
}
