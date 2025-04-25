package org.escalaralcoiaicomtat.app.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android

actual fun createHttpClient(commonConfig: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(Android) {
    commonConfig()

    engine {
        connectTimeout = 100_000
        socketTimeout = 100_000
    }
}
