package org.escalaralcoiaicomtat.app.ui.lang

expect object LanguagePreferences {
    val isLanguageChangeSupported: Boolean

    fun currentLang(): Language

    fun changeLang(lang: Language)
}
