package org.escalaralcoiaicomtat.app.ui.reusable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ContextMenuItem(
    val label: @Composable () -> String,
    val onClick: () -> Unit,
)

@Composable
expect fun ContextMenu(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    items: List<ContextMenuItem>,
    content: @Composable () -> Unit
)
