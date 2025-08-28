package org.escalaralcoiaicomtat.app.network

import kotlin.UnsupportedOperationException

actual object PlatformCarrier {
    actual fun getCarrier(): String {
        throw UnsupportedOperationException("Desktop operator fetching is currently not supported")
    }
}
