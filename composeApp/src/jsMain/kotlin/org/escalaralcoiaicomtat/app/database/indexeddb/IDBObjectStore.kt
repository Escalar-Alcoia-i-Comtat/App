package org.escalaralcoiaicomtat.app.database.indexeddb

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBObjectStore */
external interface IDBObjectStore : IDBQueryable {
    val name: String

    fun add(value: Any?): IDBRequest<IDBKey>
    fun add(value: Any?, key: IDBKey): IDBRequest<IDBKey>

    fun put(item: Any?): IDBRequest<IDBKey>
    fun put(item: Any?, key: IDBKey): IDBRequest<IDBKey>

    fun delete(key: IDBKey): IDBRequest<*>
    fun delete(key: IDBKeyRange): IDBRequest<*>

    fun clear(): IDBRequest<*>

    fun index(name: String): IDBIndex
    fun deleteIndex(name: String)
    fun createIndex(name: String, keyPath: Any?, options: IDBIndexOptions = definedExternally): IDBIndex
}
