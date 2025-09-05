package org.escalaralcoiaicomtat.app.fs

import org.escalaralcoiaicomtat.app.database.indexeddb.JsDate
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

@JsModule("jszip")
external class JSZip : JsAny {
    val files: JsAny
    fun file(name: JsString): JSZipObject?

    companion object {
        fun loadAsync(data: Uint8Array): Promise<JSZip>
    }
}

external interface JSZipObject : JsAny {
    val name: String
    val dir: Boolean
    val date: JsDate
    val comment: String?
    val unixPermissions: Short
    val dosPermissions: Byte

    fun async(type: String): Promise<Uint8Array>
}
