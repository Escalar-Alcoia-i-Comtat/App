package org.escalaralcoiaicomtat.app.database.indexeddb

import org.w3c.dom.events.Event

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBVersionChangeEvent */
abstract external class IDBVersionChangeEvent : Event {
    val oldVersion: Int
    val newVersion: Int
}

fun getDatabase(event: IDBVersionChangeEvent): IDBDatabase = js("event.target.result")
