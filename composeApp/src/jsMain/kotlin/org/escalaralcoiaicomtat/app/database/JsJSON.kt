package org.escalaralcoiaicomtat.app.database

fun jsonParse(jsonString: String): Any = js("JSON.parse(jsonString)")

fun jsonStringify(jsonObject: Any): String = js("JSON.stringify(jsonObject)")
