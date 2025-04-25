package org.escalaralcoiaicomtat.app.ui.reusable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun ContextMenu(
    modifier: Modifier,
    enabled: Boolean,
    dropdownContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf<DpOffset?>(null) }
    val density = LocalDensity.current

    DropdownMenu(
        expanded = enabled && expanded,
        onDismissRequest = { expanded = false },
        offset = offset ?: DpOffset.Unspecified,
        content = dropdownContent,
    )

    Box(
        modifier = Modifier
            .onPointerEvent(PointerEventType.Release) { event ->
                if (event.button == PointerButton.Secondary) {
                    offset = with(density) {
                        val awtEvent = event.awtEventOrNull ?: return@with null
                        val point = awtEvent.point
                        point.translate(-70, 0)
                        DpOffset(point.x.toDp(), point.y.toDp())
                    }
                    expanded = true
                }
            }
            .then(modifier),
    ) {
        content()
    }
}
