package database

import com.russhwolf.settings.ObservableSettings

expect val settings: ObservableSettings

object SettingsKeys {
    /**
     * The timestamp of the moment the last synchronization was run.
     */
    const val LAST_SYNC = "last_sync"

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
}
