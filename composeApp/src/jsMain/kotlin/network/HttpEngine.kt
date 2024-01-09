package network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.js.Js

actual fun createHttpClient(commonConfig: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(
    Js
) {
    commonConfig()
}
