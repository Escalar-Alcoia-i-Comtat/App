package database.indexeddb

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBRequest */
external open class IDBRequest<T : JsAny?> : EventTarget {
    val error: JsAny?
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
