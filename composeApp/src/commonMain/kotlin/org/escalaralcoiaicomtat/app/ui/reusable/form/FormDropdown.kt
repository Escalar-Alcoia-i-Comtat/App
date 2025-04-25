package org.escalaralcoiaicomtat.app.ui.reusable.form

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> FormDropdown(
    selection: T?,
    onSelectionChanged: (T) -> Unit,
    options: List<T>,
    label: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable (T) -> ImageVector)? = null,
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
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            maxLines = 1,
            singleLine = true,
            enabled = enabled
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = toString(option),
                            color = if (selection == option)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingIcon = icon?.invoke(option)?.let {
                        { Icon(it, toString(option)) }
                    },
                    enabled = enabled,
                    onClick = {
                        onSelectionChanged(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
