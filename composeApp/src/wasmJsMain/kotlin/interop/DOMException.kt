package interop

external interface DOMException: JsAny {
    val message: JsString
    val name: JsString
}
