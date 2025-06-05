package org.escalaralcoiaicomtat.app.ui.lang

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import org.escalaralcoiaicomtat.app.ui.Locales
import java.util.Locale

actual object LanguagePreferences {
    actual val isLanguageChangeSupported: Boolean = true

    actual fun currentLang(): Language {
        val default = Locale.getDefault().language
        return Locales.valueOf(default)
    }

    actual fun changeLang(lang: Language) {
        val locale = Locale.forLanguageTag(lang.key)
        Locale.setDefault(locale)

        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.create(locale)
        )
    }
}
