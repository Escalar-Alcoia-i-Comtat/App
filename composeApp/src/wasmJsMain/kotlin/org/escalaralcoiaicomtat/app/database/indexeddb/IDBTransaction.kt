package org.escalaralcoiaicomtat.app.database.indexeddb

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBTransaction */
external class IDBTransaction : EventTarget {
    val objectStoreNames: JsArray<JsString> // Actually a DOMStringList
    val db: IDBDatabase
    val error: JsAny?
    fun objectStore(name: String): IDBObjectStore
    fun abort()
    fun commit()
    var onabort: (Event) -> Unit
    var onerror: (Event) -> Unit
    var oncomplete: (Event) -> Unit
}
