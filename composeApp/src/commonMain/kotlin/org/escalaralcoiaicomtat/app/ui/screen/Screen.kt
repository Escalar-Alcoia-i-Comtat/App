package org.escalaralcoiaicomtat.app.ui.screen

import androidx.compose.runtime.Composable

interface Screen {
    val route: String

    @Composable
    fun Content()
}
