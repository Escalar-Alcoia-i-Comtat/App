package org.escalaralcoiaicomtat.app.ui.lang

import androidx.lifecycle.ViewModel
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.flow.map
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.ui.Locales

@OptIn(ExperimentalSettingsApi::class)
class LangViewModel: ViewModel() {
    val language = settings.getStringOrNullFlow(SettingsKeys.LANGUAGE)
        .map { lang -> lang?.let(Locales::valueOf) ?: LanguagePreferences.currentLang() }
}
