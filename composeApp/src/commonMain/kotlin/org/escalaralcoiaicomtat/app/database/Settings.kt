package org.escalaralcoiaicomtat.app.database

import com.russhwolf.settings.ObservableSettings

expect val settings: ObservableSettings

object SettingsKeys {
    /**
     * The timestamp of the moment the last synchronization was run.
     */
    const val LAST_SYNC_TIME = "last_sync_time"

    /**
     * The cause that triggered the last synchronization.
     */
    const val LAST_SYNC_CAUSE = "last_sync_cause"

    /**
     * The timestamp of the moment the last blocking synchronization was run.
     */
    const val LAST_BLOCK_SYNC_TIME = "last_block_sync_time"

    /**
     * The cause that triggered the last blocking synchronization.
     */
    const val LAST_BLOCK_SYNC_CAUSE = "last_block_sync_cause"

    /**
     * Whether the intro screen has been shown or not.
     */
    const val SHOWN_INTRO = "shown_intro"

    /**
     * On supported platforms, stores which version should not be notified of new releases.
     */
    const val SKIP_VERSION = "skip_version"

    /**
     * Stores the preferred distance units to use.
     */
    const val DISTANCE_UNITS = "distance_units"

    /**
     * Whether the distance units preference was set by the user or the system.
     *
     * Only used by Android since it allows setting distance units system-wide.
     */
    const val DISTANCE_UNITS_SYSTEM = "distance_units_system"

    /**
     * The API key that allows the user to edit the data.
     */
    const val API_KEY = "api_key"

    /**
     * The language selected for the app.
     */
    const val LANGUAGE = "language"
}
