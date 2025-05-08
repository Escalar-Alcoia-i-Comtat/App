package org.escalaralcoiaicomtat.app.database

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings

actual val settings: ObservableSettings by lazy {
    NSUserDefaultsSettings.Factory().create("escalaralcoiacomtat")
}
