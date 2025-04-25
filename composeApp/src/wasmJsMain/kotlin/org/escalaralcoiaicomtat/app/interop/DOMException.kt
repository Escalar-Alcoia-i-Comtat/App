package org.escalaralcoiaicomtat.app.interop

external interface DOMException: JsAny {
    val message: JsString
    val name: JsString
}
