package database

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import kotlinx.browser.localStorage

actual val settings: Settings by lazy {
    StorageSettings(localStorage)
}
