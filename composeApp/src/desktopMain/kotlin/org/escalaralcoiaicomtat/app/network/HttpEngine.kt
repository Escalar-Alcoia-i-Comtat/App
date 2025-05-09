package org.escalaralcoiaicomtat.app.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO

actual fun createHttpClient(commonConfig: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(CIO) {
    commonConfig()
}
