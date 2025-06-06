package org.escalaralcoiaicomtat.app.database.indexeddb

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBDatabase/createObjectStore#parameters */
external interface IDBObjectStoreOptions {
    val autoIncrement: Boolean?
    val keyPath: Any
}

fun IDBObjectStoreOptions(
    autoIncrement: Boolean,
): IDBObjectStoreOptions = js("({ autoIncrement: autoIncrement })")

fun IDBObjectStoreOptions(
    keyPath: Any?,
): IDBObjectStoreOptions = js("({ keyPath: keyPath })")
