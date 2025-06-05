package org.escalaralcoiaicomtat.app.ui.lang

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LocalizedApp(
    model: LangViewModel = viewModel { LangViewModel() },
    content: @Composable () -> Unit,
) {
    val language by model.language.collectAsState(Language.English)

    CompositionLocalProvider(
        LocalLanguage provides language,
        content = content
    )
}
