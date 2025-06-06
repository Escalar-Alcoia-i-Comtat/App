package org.escalaralcoiaicomtat.app.database

import org.escalaralcoiaicomtat.app.database.indexeddb.IDBTransaction
import org.escalaralcoiaicomtat.app.exception.TransactionAbortedException
import org.escalaralcoiaicomtat.app.exception.TransactionException
import org.w3c.dom.events.Event
import kotlin.js.Promise

fun IDBTransaction.promise(): Promise<Event> {
    return Promise<Event> { resolve, reject ->
        onerror = { reject(TransactionException(error)) }
        onabort = { reject(TransactionAbortedException()) }
        oncomplete = { resolve(it) }
    }
}
