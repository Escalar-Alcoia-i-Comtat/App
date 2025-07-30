package org.escalaralcoiaicomtat.app.utils

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.ktor.client.request.forms.FormBuilder
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.defaultForFileExtension
import kotlinx.serialization.SerializationStrategy
import org.escalaralcoiaicomtat.app.json

/**
 * Appends a new value at [key], and sets the value to `\u0000`.
 */
fun FormBuilder.appendRemoval(key: String) {
    append(key, "\u0000")
}

fun FormBuilder.appendOrRemove(key: String, value: String?) {
    if (value == null) appendRemoval(key)
    else append(key, value)
}

fun FormBuilder.appendOrRemove(key: String, value: Number?) {
    if (value == null) appendRemoval(key)
    else append(key, value)
}

fun FormBuilder.appendOrRemove(key: String, value: Boolean?) {
    if (value == null) appendRemoval(key)
    else append(key, value)
}

fun <T> FormBuilder.appendSerializable(key: String, value: T, serializer: SerializationStrategy<T>, headers: Headers = Headers.Empty) {
    val str = json.encodeToString(serializer, value).trim('"')
    append(key, str, headers)
}

fun <T> FormBuilder.appendOrRemove(key: String, value: T?, serializer: SerializationStrategy<T>) {
    val str = value?.let { json.encodeToString(serializer, it).trim('"') }
    appendOrRemove(key, str)
}

fun FormBuilder.append(key: String, file: PlatformFile, bytes: ByteArray) {
    append(
        key,
        bytes,
        Headers.build {
            append(HttpHeaders.ContentType, ContentType.defaultForFileExtension(file.extension).toString())
            append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
        },
    )
}
