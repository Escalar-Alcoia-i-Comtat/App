package platform

import kotlinx.browser.window

actual class LifecycleManager {
    actual fun finish() {
        window.close()
    }
}
