package org.escalaralcoiaicomtat.app.ui.reusable.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun <T : Any> FormOptionPicker(
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
    if (options.size > 4) {
        FormDropdown(
            selection,
            onSelectionChanged,
            options,
            label,
            modifier,
            enabled,
            leadingIcon,
            icon,
            color,
            toString
        )
    } else {
        Column(modifier = modifier) {
            label?.let {
                Text(
                    text = label,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                for ((idx, option) in options.withIndex()) {
                    SegmentedButton(
                        selected = selection == option,
                        label = { Text(toString(option)) },
                        shape = SegmentedButtonDefaults.itemShape(idx, options.size),
                        onClick = { onSelectionChanged(option) }
                    )
                }
            }
        }
    }
}
