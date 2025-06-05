package org.escalaralcoiaicomtat.app.ui.lang

actual object LanguagePreferences {
    actual val isLanguageChangeSupported: Boolean = true

    actual fun changeLang(lang: Language) {
        NSUserDefaults.standardUserDefaults.setObject(arrayListOf(lang.key), "AppleLanguages")
    }
}
