package org.escalaralcoiaicomtat.app.exception

import org.escalaralcoiaicomtat.app.interop.DOMException

class JavaScriptException(name: String, message: String) : RuntimeException("DOMException: $name - $message") {
    companion object {
        fun DOMException.toJavaScriptException() = JavaScriptException(this)
    }

    constructor(domException: DOMException) : this(
        domException.name.toString(),
        domException.message.toString()
    )
}
