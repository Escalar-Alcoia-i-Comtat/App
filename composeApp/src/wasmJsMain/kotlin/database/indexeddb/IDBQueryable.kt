package database.indexeddb

/** Pseudo-interface for the shared query functionality between [IDBIndex] and [IDBObjectStore]. */
external interface IDBQueryable {

    fun count(): IDBRequest<JsNumber>
    fun count(key: IDBKey?): IDBRequest<JsNumber>
    fun count(key: IDBKeyRange): IDBRequest<JsNumber>

    fun get(key: IDBKey): IDBRequest<*>
    fun get(key: IDBKeyRange): IDBRequest<*>

    fun getAll(): IDBRequest<JsArray<*>>
    fun getAll(query: IDBKey?): IDBRequest<JsArray<*>>
    fun getAll(query: IDBKey?, count: Int): IDBRequest<JsArray<*>>

    fun getAll(query: IDBKeyRange?): IDBRequest<JsArray<*>>
    fun getAll(query: IDBKeyRange?, count: Int): IDBRequest<JsArray<*>>

    fun getAllKeys(): IDBRequest<JsArray<IDBKey>>
    fun getAllKeys(query: IDBKey?): IDBRequest<JsArray<IDBKey>>
    fun getAllKeys(query: IDBKey?, count: Int): IDBRequest<JsArray<IDBKey>>

    fun getAllKeys(query: IDBKeyRange?): IDBRequest<JsArray<IDBKey>>
    fun getAllKeys(query: IDBKeyRange?, count: Int): IDBRequest<JsArray<IDBKey>>

    fun getKey(query: IDBKey): IDBRequest<IDBKey?>
    fun getKey(query: IDBKeyRange): IDBRequest<IDBKey?>

    fun openCursor(): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBKey?): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBKey?, direction: String): IDBRequest<IDBCursorWithValue?>

    fun openCursor(query: IDBKeyRange?): IDBRequest<IDBCursorWithValue?>
    fun openCursor(query: IDBKeyRange?, direction: String): IDBRequest<IDBCursorWithValue?>

    fun openKeyCursor(): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBKey?): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBKey?, direction: String): IDBRequest<IDBCursor?>

    fun openKeyCursor(query: IDBKeyRange?): IDBRequest<IDBCursor?>
    fun openKeyCursor(query: IDBKeyRange?, direction: String): IDBRequest<IDBCursor?>
}
