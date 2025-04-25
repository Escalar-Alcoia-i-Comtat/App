package org.escalaralcoiaicomtat.app.platform

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import org.escalaralcoiaicomtat.app.platform.Updates.updateAvailable

actual object Updates {
    /**
     * Whether the platform supports updates.
     */
    actual val updatesSupported: Boolean get() = false // web is always up to date

    /**
     * Whether there's an update available.
     */
    actual val updateAvailable: MutableStateFlow<Boolean> get() = MutableStateFlow(false)

    /**
     * If supported, holds the name of the latest version available. Only applies if
     * [updateAvailable] is true.
     */
    actual val latestVersion: MutableStateFlow<String?> get() = MutableStateFlow(null)

    /**
     * Requests the device to update to the latest version available.
     *
     * @return The job that is performing the update, or null if updates are not available.
     */
    actual fun requestUpdate(): Job? = null
}