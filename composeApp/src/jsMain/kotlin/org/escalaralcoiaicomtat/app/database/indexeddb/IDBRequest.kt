package org.escalaralcoiaicomtat.app.database.indexeddb

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBRequest */
open external class IDBRequest<T : Any?> : EventTarget {
    val error: Exception?
    val transaction: IDBTransaction?
    val result: T
    var onerror: (Event) -> Unit
    var onsuccess: (Event) -> Unit
}

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBOpenDBRequest */
external class IDBOpenDBRequest : IDBRequest<IDBDatabase> {
    var onblocked: (Event) -> Unit
    var onupgradeneeded: (IDBVersionChangeEvent) -> Unit
}
