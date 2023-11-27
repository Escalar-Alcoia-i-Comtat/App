package exception

import io.ktor.http.Url

/**
 * Identifies an error thrown by the server.
 */
class ServerException(
    val code: Int,
    message: String?,
    val url: Url?
): RuntimeException("Server responded with an exception.\nUrl: $url\nCode: $code. Message: $message")
