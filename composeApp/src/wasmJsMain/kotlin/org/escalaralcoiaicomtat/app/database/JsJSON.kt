package org.escalaralcoiaicomtat.app.database

fun jsonParse(jsonString: String): JsAny = js("JSON.parse(jsonString)")

fun jsonStringify(jsonObject: JsAny): JsString = js("JSON.stringify(jsonObject)")
