package org.escalaralcoiaicomtat.app.network

import build.BuildKonfig
import io.github.aakira.napier.Napier
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.content.PartData
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.charsets.Charsets
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import org.escalaralcoiaicomtat.app.exception.ServerException
import org.escalaralcoiaicomtat.app.json
import org.escalaralcoiaicomtat.app.network.response.DataResponse
import org.escalaralcoiaicomtat.app.network.response.ErrorResponse
import org.escalaralcoiaicomtat.app.network.response.data.DataResponseType
import org.escalaralcoiaicomtat.app.platform.httpCacheStorage

abstract class Backend {
    companion object {
        private const val BASE_URL_FALLBACK = "https://backend.escalaralcoiaicomtat.org"
        val baseUrl = BuildKonfig.BASE_URL.takeUnless { it.isNullOrBlank() } ?: BASE_URL_FALLBACK
    }

    protected val client = createHttpClient {
        install(ContentNegotiation) {
            json(
                json = json
            )
        }
        install(HttpCache) {
            val storage = try {
                httpCacheStorage("backend")
            } catch (_: UnsupportedOperationException) {
                null
            }
            if (storage != null) {
                publicStorage(storage)
            }
        }
    }

    init {
        Napier.i { "Base URL: $baseUrl" }
    }

    /**
     * If the request was successful, extracts a [DataResponse] with type [DT] from its body.
     * Otherwise extracts the error given, and throws it as a [ServerException].
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    private suspend fun <DT : DataResponseType> decodeBody(
        response: HttpResponse,
        deserializer: DeserializationStrategy<DT>,
        hasData: Boolean = true,
    ): DT? {
        return try {
            val url = response.request.url
            val status = response.status.value
            val body = response.bodyAsText(fallbackCharset = Charsets.UTF_8)

            /*Napier.v(tag = "Backend") {
                "Got response from server ($url - $status). Raw body: $body"
            }*/

            if (status in 200..299) {
                if (hasData) {
                    DataResponse.decode(body, deserializer)
                } else {
                    DataResponse.decode(body)
                    null
                }.also {
                    Napier.d(tag = "Backend") {
                        "Server responded successfully to ${response.request.url}. Status: $status"
                    }
                }
            } else {
                Napier.e(tag = "Backend") {
                    "Server responded with an exception.\nUrl: $url\nCode: $status. Body: $body"
                }
                json.decodeFromString<ErrorResponse>(body).throwException(url)
            }
        } catch (exception: NoTransformationFoundException) {
            Napier.e(tag = "Backend", throwable = exception) {
                "Got unexpected response from server. It cannot be parsed."
            }
            error("Received an unhandleable response from the server.")
        } catch (exception: Exception) {
            Napier.e(tag = "Backend", throwable = exception) {
                "Could not decode server's body. Unknown exception."
            }
            throw exception
        }
    }

    /**
     * Runs an HTTP GET request to the backend server, at the given path.
     *
     * @param pathComponents The components of the path to request. If not [String], will be
     * converted into one automatically using the class's `toString` function.
     * @param progress If not null, will be called with the progress of the request.
     *
     * @return The response given by the server, of the type desired.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    protected suspend fun <DT : DataResponseType> get(
        deserializer: DeserializationStrategy<DT>,
        vararg pathComponents: Any,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): DT {
        var length: Long = 0
        progress?.invoke(0, length)
        val response = client.get(
            URLBuilder(baseUrl)
                .appendPathSegments(pathComponents.map { it.toString() })
                .build()
                .also { Napier.v("GET :: $it") }
        ) {
            onDownload { bytesSentTotal, contentLength ->
                contentLength ?: return@onDownload
                length = contentLength
                progress?.invoke(bytesSentTotal, contentLength)
            }
        }
        progress?.invoke(length, length)
        return decodeBody(response, deserializer)!!
    }

    /**
     * Runs an HTTP GET request to the backend server, at the given path.
     *
     * If the server responds with error code `2` (_The requested data was not found_), the function
     * returns `null`.
     * Any other error will be thrown.
     *
     * @param pathComponents The components of the path to request. If not [String], will be
     * converted into one automatically using the class's `toString` function.
     * @param progress If not null, will be called with the progress of the request.
     *
     * @return The response given by the server, of the type desired.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    protected suspend fun <DT : DataResponseType> getOrNull(
        deserializer: DeserializationStrategy<DT>,
        vararg pathComponents: Any,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null
    ): DT? {
        try {
            return get(deserializer, *pathComponents, progress = progress)
        } catch (e: ServerException) {
            if (e.code == 2) { // The requested data was not found.
                return null
            }
            throw e
        }
    }

    /**
     * Makes a POST request to the endpoint defined by the path components given.
     *
     * @param serializer The serializer to use for the response.
     * @param pathComponents The components of the path to request. If not [String], will be
     * converted into one automatically using the class's `toString` function.
     * @param progress If not null, will be called with the progress of the request.
     *
     * @return The response given by the server, of the type desired.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    protected suspend fun <DT: DataResponseType> submitForm(
        serializer: KSerializer<DT>,
        vararg pathComponents: Any,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
        requestBuilder: HttpRequestBuilder.() -> Unit = {},
        formBuilder: FormBuilder.() -> Unit
    ): DT {
        var length: Long = 0
        progress?.invoke(0, length)

        val url = URLBuilder(baseUrl)
            .appendPathSegments(pathComponents.map { it.toString() })
            .buildString()
        Napier.v("POST :: $url")

        val formData = formData(formBuilder).also { parts ->
            val data = StringBuilder()
            for (part in parts) {
                if (part is PartData.FormItem) {
                    data.appendLine("- ${part.name}\n  ${part.value}")
                } else {
                    data.appendLine("- ${part.name} - Type: ${part.contentType}")
                }
            }
            Napier.i { "Making request to $url\nData:\n$data" }
        }
        require(formData.isNotEmpty()) { "Form data is empty, nothing to send." }

        val response = client.submitFormWithBinaryData(
            url = url,
            formData = formData,
        ) {
            requestBuilder()
            onDownload { bytesSentTotal, contentLength ->
                contentLength ?: return@onDownload
                length = contentLength
                progress?.invoke(bytesSentTotal, contentLength)
            }
        }
        progress?.invoke(length, length)
        return decodeBody(response, serializer)!!
    }

    /**
     * Makes a DELETE request to the endpoint defined by the path components given.
     *
     * @param serializer The serializer to use for the response.
     * @param pathComponents The components of the path to request. If not [String], will be
     * converted into one automatically using the class's `toString` function.
     * @param progress If not null, will be called with the progress of the request.
     *
     * @return The response given by the server, of the type desired.
     *
     * @throws ServerException If the server responded with an exception, or the body of the body of
     * the response didn't match a [DataResponse].
     * @throws IllegalStateException If the server gave a response that could not be handled.
     */
    protected suspend fun <DT: DataResponseType> delete(
        serializer: KSerializer<DT>,
        vararg pathComponents: Any,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
        requestBuilder: HttpRequestBuilder.() -> Unit = {}
    ) {
        var length: Long = 0
        progress?.invoke(0, length)
        val response = client.delete(
            url = URLBuilder(baseUrl)
                .appendPathSegments(pathComponents.map { it.toString() })
                .build()
                .also { Napier.v("PATCH :: $it") },
        ) {
            requestBuilder()
            onDownload { bytesSentTotal, contentLength ->
                contentLength ?: return@onDownload
                length = contentLength
                progress?.invoke(bytesSentTotal, contentLength)
            }
        }
        progress?.invoke(length, length)
        decodeBody(response, serializer, false)
    }
}
