package org.escalaralcoiaicomtat.app.database.indexeddb

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBIndex */
external interface IDBIndex : IDBQueryable {
    val name: String
    val objectStore: IDBObjectStore
}
