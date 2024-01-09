package database

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings

actual val settings: Settings by lazy {
    NSUserDefaultsSettings.Factory().create("escalaralcoiacomtat")
}
