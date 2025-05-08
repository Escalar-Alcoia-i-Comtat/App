package org.escalaralcoiaicomtat.app.platform

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import org.escalaralcoiaicomtat.app.platform.Updates.updateAvailable

expect object Updates {
    /**
     * Whether the platform supports updates.
     */
    val updatesSupported: Boolean

    /**
     * Whether there's an update available.
     */
    val updateAvailable: MutableStateFlow<Boolean>

    /**
     * If any, the error that occurred during the update or the checking for updates.
     */
    val updateError: MutableStateFlow<String?>

    /**
     * If supported, holds the name of the latest version available. Only applies if
     * [updateAvailable] is true.
     */
    val latestVersion: MutableStateFlow<String?>

    /**
     * Requests the device to update to the latest version available.
     *
     * @return The job that is performing the update, or null if updates are not available.
     */
    fun requestUpdate(): Job?
}
