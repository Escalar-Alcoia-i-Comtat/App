package org.escalaralcoiaicomtat.app.exception

open class IDBException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
