package database

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.escalaralcoiaicomtat.android.applicationContext

actual val settings: Settings by lazy {
    SharedPreferencesSettings.Factory(applicationContext).create("escalaralcoiaicomtat")
}
