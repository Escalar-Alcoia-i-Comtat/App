package database.indexeddb

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBObjectStore */
external interface IDBObjectStore : IDBQueryable {
    val name: String

    fun add(value: JsAny?): IDBRequest<IDBKey>
    fun add(value: JsAny?, key: IDBKey): IDBRequest<IDBKey>

    fun put(item: JsAny?): IDBRequest<IDBKey>
    fun put(item: JsAny?, key: IDBKey): IDBRequest<IDBKey>

    fun delete(key: IDBKey): IDBRequest<*>
    fun delete(key: IDBKeyRange): IDBRequest<*>

    fun clear(): IDBRequest<*>

    fun index(name: String): IDBIndex
    fun deleteIndex(name: String)
    fun createIndex(name: String, keyPath: JsAny?, options: IDBIndexOptions = definedExternally): IDBIndex
}
