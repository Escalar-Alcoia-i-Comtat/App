package exception

import interop.DOMException

class JavaScriptException(name: String, message: String) : RuntimeException("DOMException: $name - $message") {
    companion object {
        fun DOMException.toJavaScriptException() = JavaScriptException(this)
    }

    constructor(domException: DOMException) : this(
        domException.name.toString(),
        domException.message.toString()
    )
}
