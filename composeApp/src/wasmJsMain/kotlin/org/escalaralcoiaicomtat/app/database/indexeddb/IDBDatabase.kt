package org.escalaralcoiaicomtat.app.database.indexeddb

import org.w3c.dom.events.EventTarget

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBDatabase */
external class IDBDatabase : EventTarget {
    val name: String
    val version: Int
    val objectStoreNames: JsArray<JsString>
    fun close()
    fun createObjectStore(name: String): IDBObjectStore
    fun createObjectStore(name: String, options: IDBObjectStoreOptions?): IDBObjectStore
    fun deleteObjectStore(name: String)

    fun transaction(
        storeName: String,
        mode: String,
        options: IDBTransactionOptions = definedExternally,
    ): IDBTransaction

    fun transaction(
        storeNames: JsArray<JsString>,
        mode: String,
        options: IDBTransactionOptions = definedExternally,
    ): IDBTransaction
}
