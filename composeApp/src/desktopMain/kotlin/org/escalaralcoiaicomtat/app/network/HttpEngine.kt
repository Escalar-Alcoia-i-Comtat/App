package org.escalaralcoiaicomtat.app.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.java.Java

actual fun createHttpClient(commonConfig: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(Java) {
    commonConfig()

    engine {
        pipelining = true
        protocolVersion = java.net.http.HttpClient.Version.HTTP_2
    }
}
