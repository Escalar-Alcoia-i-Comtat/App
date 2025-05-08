package org.escalaralcoiaicomtat.app.platform

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import org.escalaralcoiaicomtat.app.platform.Updates.updateAvailable

actual object Updates {
    /**
     * Whether the platform supports updates.
     */
    actual val updatesSupported: Boolean = false

    /**
     * Whether there's an update available.
     */
    actual val updateAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * If any, the error that occurred during the update or the checking for updates.
     */
    actual val updateError: MutableStateFlow<String?> = MutableStateFlow(null)

    /**
     * Requests the device to update to the latest version available.
     *
     * @return The job that is performing the update, or null if updates are not available.
     */
    actual fun requestUpdate(): Job? = null

    /**
     * If supported, holds the name of the latest version available. Only applies if
     * [updateAvailable] is true.
     */
    actual val latestVersion: MutableStateFlow<String?> = MutableStateFlow(null)
}
