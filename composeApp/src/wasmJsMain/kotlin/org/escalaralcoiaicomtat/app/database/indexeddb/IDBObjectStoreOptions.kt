package org.escalaralcoiaicomtat.app.database.indexeddb

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBDatabase/createObjectStore#parameters */
external interface IDBObjectStoreOptions {
    val autoIncrement: Boolean?
    val keyPath: JsAny
}

fun IDBObjectStoreOptions(
    autoIncrement: Boolean,
): IDBObjectStoreOptions = js("({ autoIncrement: autoIncrement })")

fun IDBObjectStoreOptions(
    keyPath: JsAny?,
): IDBObjectStoreOptions = js("({ keyPath: keyPath })")
