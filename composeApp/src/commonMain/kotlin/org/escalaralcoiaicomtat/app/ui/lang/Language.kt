package org.escalaralcoiaicomtat.app.ui.lang

sealed class Language(val key: String, val displayName: String, val localeDisplayName: String) {
    object Catalan : Language("ca", "Catalan", "Català")
    object English : Language("en", "English", "English")
    object French : Language("fr", "French", "Français")
    object Spanish : Language("es", "Spanish", "Español")

    companion object {
        val all: List<Language> get() = listOf(Catalan, English, French, Spanish)
    }
}
