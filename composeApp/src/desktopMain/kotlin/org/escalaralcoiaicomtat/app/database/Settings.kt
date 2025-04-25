package org.escalaralcoiaicomtat.app.database

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings

actual val settings: ObservableSettings by lazy {
    PreferencesSettings.Factory().create("escalaralcoiaicomtat")
}
