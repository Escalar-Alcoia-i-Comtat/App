package org.escalaralcoiaicomtat.app.network

actual object PlatformCarrier {
    actual fun getCarrier(): String {
        throw UnsupportedOperationException("WASM operator fetching is currently not supported")
    }
}
