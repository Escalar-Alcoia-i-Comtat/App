package database

import com.russhwolf.settings.ObservableSettings

expect val settings: ObservableSettings

object SettingsKeys {
    /**
     * The timestamp of the moment the last synchronization was run.
     */
    const val LAST_SYNC = "last_sync"
}