package org.escalaralcoiaicomtat.app.ui.lang

import androidx.compose.ui.text.intl.Locale
import org.escalaralcoiaicomtat.app.ui.Locales
import platform.Foundation.NSUserDefaults

actual object LanguagePreferences {
    actual val isLanguageChangeSupported: Boolean = true

    actual fun currentLang(): Language {
        val default = Locale.current.toLanguageTag()
        return Locales.valueOf(default)
    }

    actual fun changeLang(lang: Language) {
        NSUserDefaults.standardUserDefaults.setObject(arrayListOf(lang.key), "AppleLanguages")
    }
}
