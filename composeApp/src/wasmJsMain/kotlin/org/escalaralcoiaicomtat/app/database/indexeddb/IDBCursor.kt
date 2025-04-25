package org.escalaralcoiaicomtat.app.database.indexeddb

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBCursor */
external interface IDBCursor : JsAny {
    val key: IDBKey
    val primaryKey: IDBKey

    fun advance(count: Int)

    fun `continue`()
    fun `continue`(key: IDBKey)

    fun continuePrimaryKey(key: IDBKey, primaryKey: IDBKey)
}

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBCursorWithValue */
external interface IDBCursorWithValue : IDBCursor {
    val value: JsAny?

    fun delete(): IDBRequest<*>
    fun update(value: JsAny?): IDBRequest<IDBKey>
}
