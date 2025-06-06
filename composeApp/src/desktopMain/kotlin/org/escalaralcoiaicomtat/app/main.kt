package org.escalaralcoiaicomtat.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.cache.StorageProvider
import org.escalaralcoiaicomtat.app.cache.storageProvider
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.getDatabaseBuilder
import org.escalaralcoiaicomtat.app.database.roomDatabaseBuilder
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.platform.Updates
import org.escalaralcoiaicomtat.app.sync.SyncManager
import org.escalaralcoiaicomtat.app.ui.Locales
import org.escalaralcoiaicomtat.app.ui.lang.LanguagePreferences
import org.escalaralcoiaicomtat.app.ui.state.KeyEventCollector
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Initialize the logging library
    Napier.base(DebugAntilog())

    initializeSentry()

    storageProvider = StorageProvider()
    roomDatabaseBuilder = getDatabaseBuilder()

    SyncManager.schedule()

    CoroutineScope(Dispatchers.IO).launch {
        Updates.checkForUpdates()
    }

    // Update the default language
    settings.getStringOrNull(SettingsKeys.LANGUAGE)?.let { langKey ->
        val locale = Locales.valueOf(langKey)
        LanguagePreferences.changeLang(locale)
    }

    application {
        Window(
            title = "Escalar Alcoià i Comtat",
            icon = painterResource(Res.drawable.icon),
            onCloseRequest = ::exitApplication,
            onPreviewKeyEvent = { KeyEventCollector.emit(it) }
        ) {
            AppRoot()
        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    AppRoot()
}
