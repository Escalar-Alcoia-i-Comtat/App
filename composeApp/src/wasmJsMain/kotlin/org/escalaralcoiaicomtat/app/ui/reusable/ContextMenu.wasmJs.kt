package org.escalaralcoiaicomtat.app.ui.reusable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
actual fun ContextMenu(
    modifier: Modifier,
    enabled: Boolean,
    dropdownContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        Box(
            modifier = Modifier
                .combinedClickable(
                    enabled = enabled,
                    onLongClickLabel = null,
                    onLongClick = { expanded = true },
                    onClick = {}
                )
                .then(modifier),
        ) {
            content()
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) { dropdownContent() }
    }
}
