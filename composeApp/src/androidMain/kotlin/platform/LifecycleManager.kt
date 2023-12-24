package platform

import org.escalaralcoiaicomtat.android.MainActivity

actual class LifecycleManager {
    actual fun finish() {
        MainActivity.instance?.finish()
    }
}
