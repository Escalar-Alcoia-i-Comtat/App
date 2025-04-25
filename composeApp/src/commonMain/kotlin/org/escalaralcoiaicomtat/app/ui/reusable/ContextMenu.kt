package org.escalaralcoiaicomtat.app.ui.reusable

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ContextMenu(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dropdownContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
)
