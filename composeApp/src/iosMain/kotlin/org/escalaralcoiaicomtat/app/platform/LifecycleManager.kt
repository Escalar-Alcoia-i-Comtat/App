package org.escalaralcoiaicomtat.app.platform

import kotlin.system.exitProcess

actual class LifecycleManager {
    actual fun finish() {
        exitProcess(-1)
    }
}
