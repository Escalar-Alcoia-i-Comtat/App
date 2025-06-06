package org.escalaralcoiaicomtat.app.ui.lang

import kotlinx.browser.window
import org.escalaralcoiaicomtat.app.ui.Locales

actual object LanguagePreferences {
    actual val isLanguageChangeSupported: Boolean = false

    actual fun currentLang(): Language {
        val default = window.navigator.language
        return Locales.valueOf(default)
    }

    actual fun changeLang(lang: Language) {
        throw UnsupportedOperationException()
    }
}
