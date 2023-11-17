package network.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ErrorResponse(
    val code: Int,
    val message: String? = null
): Response(false) {
    @Transient
    val exception: RuntimeException = RuntimeException("Server responded with an exception. Code: $code. Message: $message")
}
