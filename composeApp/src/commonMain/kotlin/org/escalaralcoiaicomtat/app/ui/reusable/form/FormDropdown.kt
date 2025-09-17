package org.escalaralcoiaicomtat.app.ui.reusable.form

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun <T : Any> FormDropdown(
    selection: T?,
    onSelectionChanged: (T) -> Unit,
    options: List<T>,
    label: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon:  @Composable (() -> Unit)? = null,
    icon: (@Composable (T) -> ImageVector)? = null,
    color: (@Composable (T) -> Color)? = null,
    toString: @Composable (T) -> String = { it.toString() }
) {
    FormDropdown(
        selection = selection,
        onSelectionChanged = { onSelectionChanged(it!!) },
        options = options,
        label = label,
        modifier = modifier,
        enabled = enabled,
        canUnselect = false,
        leadingIcon = leadingIcon,
        icon = icon,
        color = color,
        toString = toString
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> FormDropdown(
    selection: T?,
    onSelectionChanged: (T?) -> Unit,
    options: List<T>,
    label: String?,
    canUnselect: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon:  @Composable (() -> Unit)? = null,
    icon: (@Composable (T) -> ImageVector)? = null,
    color: (@Composable (T) -> Color)? = null,
    toString: @Composable (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selection?.let { toString(it) } ?: "",
            onValueChange = { },
            readOnly = true,
            label = label?.let { { Text(it) } },
            leadingIcon = leadingIcon,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            maxLines = 1,
            singleLine = true,
            enabled = enabled
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                val textColor = color?.invoke(option) ?: MaterialTheme.colorScheme.onSurface
                DropdownMenuItem(
                    text = {
                        Text(
                            text = toString(option),
                            color = if (selection == option)
                                MaterialTheme.colorScheme.primary
                            else
                                textColor
                        )
                    },
                    leadingIcon = icon?.invoke(option)?.let {
                        { Icon(it, toString(option)) }
                    },
                    enabled = enabled,
                    onClick = {
                        if (canUnselect && option == selection) {
                            onSelectionChanged(null)
                        } else {
                            onSelectionChanged(option)
                        }
                        expanded = false
                    }
                )
            }
        }
    }
}
