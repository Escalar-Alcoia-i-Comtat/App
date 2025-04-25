import build.BuildKonfig
import io.sentry.kotlin.multiplatform.Sentry

actual fun initializeSentry() {
    Sentry.init { options ->
        options.dsn = BuildKonfig.SENTRY_DSN
    }
}
