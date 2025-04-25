package org.escalaralcoiaicomtat.app.network.response

import io.ktor.http.Url
import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.exception.ServerException

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
