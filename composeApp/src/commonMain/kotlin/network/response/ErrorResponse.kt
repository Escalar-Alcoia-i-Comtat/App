package network.response

import exception.ServerException
import io.ktor.http.Url
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: Error,
    val success: Boolean = false
): Response {
    @Serializable
    data class Error(
        val code: Int,
        val message: String? = null
    )

    fun <R> throwException(url: Url?): R {
        throw with(error) { ServerException(code, message, url) }
    }
}
