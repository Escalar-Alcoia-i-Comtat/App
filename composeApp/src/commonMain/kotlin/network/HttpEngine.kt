package network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

expect fun createHttpClient(commonConfig: HttpClientConfig<*>.() -> Unit = {}): HttpClient
