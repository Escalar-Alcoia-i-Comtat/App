package database

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.escalaralcoiaicomtat.android.applicationContext

actual val settings: ObservableSettings by lazy {
    SharedPreferencesSettings.Factory(applicationContext).create("escalaralcoiaicomtat")
}
