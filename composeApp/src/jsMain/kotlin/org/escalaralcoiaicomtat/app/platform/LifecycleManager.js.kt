package org.escalaralcoiaicomtat.app.platform

import kotlinx.browser.window

actual class LifecycleManager actual constructor() {
    actual fun finish() {
        window.close()
    }
}
