package org.escalaralcoiaicomtat.app.platform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.android.MainActivity
import org.escalaralcoiaicomtat.app.platform.Updates.updateAvailable

actual object Updates {
    /**
     * Whether the platform supports updates.
     */
    actual val updatesSupported: Boolean = true

    /**
     * Whether there's an update available.
     */
    actual val updateAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * Requests the device to update to the latest version available.
     *
     * @return The job that is performing the update, or null if updates are not available.
     */
    actual fun requestUpdate(): Job? = CoroutineScope(Dispatchers.Main).launch {
        val activity = MainActivity.instance ?: return@launch
        activity.installUpdate()
    }

    /**
     * If supported, holds the name of the latest version available. Only applies if
     * [updateAvailable] is true.
     */
    actual val latestVersion: MutableStateFlow<String?> = MutableStateFlow(null)
}
