package database

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings

actual val settings: Settings by lazy {
    PreferencesSettings.Factory().create("escalaralcoiaicomtat")
}
