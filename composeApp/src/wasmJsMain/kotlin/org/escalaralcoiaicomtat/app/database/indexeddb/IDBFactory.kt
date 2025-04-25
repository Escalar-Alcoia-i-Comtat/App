package org.escalaralcoiaicomtat.app.database.indexeddb

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBFactory */
external interface IDBFactory {
    fun open(name: String, version: Int): IDBOpenDBRequest
    fun deleteDatabase(name: String): IDBOpenDBRequest
}
