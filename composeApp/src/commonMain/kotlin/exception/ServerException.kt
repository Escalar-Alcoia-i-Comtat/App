package exception

/**
 * Identifies an error thrown by the server.
 */
class ServerException(
    val code: Int,
    message: String?
): RuntimeException("Server responded with an exception. Code: $code. Message: $message")
