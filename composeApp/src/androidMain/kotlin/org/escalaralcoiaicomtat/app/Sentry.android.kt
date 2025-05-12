package org.escalaralcoiaicomtat.app

import build.BuildKonfig
import io.sentry.kotlin.multiplatform.Sentry

actual fun initializeSentry() {
    Sentry.init { options ->
        options.dsn = BuildKonfig.SENTRY_DSN

        // Enable tracing
        options.tracesSampleRate = 1.0
    }
}
