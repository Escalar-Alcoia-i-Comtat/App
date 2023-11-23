package network.response

import exception.ServerException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ErrorResponse(
    val code: Int,
    val message: String? = null
): Response(false) {
    /**
     * Provides an exception based on the response that can be thrown.
     */
    @Transient
    val exception: ServerException = ServerException(code, message)
}
