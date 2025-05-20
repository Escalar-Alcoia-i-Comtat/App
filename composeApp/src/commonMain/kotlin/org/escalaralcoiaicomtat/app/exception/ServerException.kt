package org.escalaralcoiaicomtat.app.exception

import io.ktor.http.HttpMethod
import io.ktor.http.Url

/**
 * Identifies an error thrown by the server.
 */
class ServerException(
    val code: Int,
    message: String?,
    val method: HttpMethod?,
    val url: Url?
): RuntimeException("Server responded with an exception.\nMethod: $method\nUrl: $url\nCode: $code. Message: $message")
