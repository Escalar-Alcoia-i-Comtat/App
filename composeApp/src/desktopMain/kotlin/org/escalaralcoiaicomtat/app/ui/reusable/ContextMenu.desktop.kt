package org.escalaralcoiaicomtat.app.ui.reusable

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.foundation.ContextMenuItem as DesktopContextMenuItem

@Composable
fun ContextMenuItem.toDesktopContextMenuItem(): DesktopContextMenuItem = DesktopContextMenuItem(
    label = label(),
    onClick = onClick,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun ContextMenu(
    modifier: Modifier,
    enabled: Boolean,
    items: List<ContextMenuItem>,
    content: @Composable () -> Unit
) {
    val mappedItems = items.map { it.toDesktopContextMenuItem() }
    ContextMenuArea(
        enabled = enabled,
        items = { mappedItems },
        content = content,
    )
}
