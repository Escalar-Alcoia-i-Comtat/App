package org.escalaralcoiaicomtat.app.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClient(commonConfig: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(Darwin) {
    commonConfig()

    engine {
        pipelining = true
    }
}
