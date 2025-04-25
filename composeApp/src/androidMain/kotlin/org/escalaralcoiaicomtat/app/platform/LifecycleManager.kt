package org.escalaralcoiaicomtat.app.platform

import org.escalaralcoiaicomtat.android.MainActivity

actual class LifecycleManager {
    actual fun finish() {
        MainActivity.instance?.finish()
    }
}
