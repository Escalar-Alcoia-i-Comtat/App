package org.escalaralcoiaicomtat.app.database.indexeddb

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import kotlin.js.collections.JsArray

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBTransaction */
external class IDBTransaction : EventTarget {
    val objectStoreNames: JsArray<String> // Actually a DOMStringList
    val db: IDBDatabase
    val error: Exception? // Actually a DOMException
    fun objectStore(name: String): IDBObjectStore
    fun abort()
    fun commit()
    var onabort: (Event) -> Unit
    var onerror: (Event) -> Unit
    var oncomplete: (Event) -> Unit
}
