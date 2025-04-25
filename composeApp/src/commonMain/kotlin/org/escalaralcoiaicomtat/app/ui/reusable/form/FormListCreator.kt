package org.escalaralcoiaicomtat.app.ui.reusable.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> FormListCreator(
    elements: List<T>,
    onElementsChange: (List<T>) -> Unit,
    constructor: () -> T,
    creator: @Composable ColumnScope.(value: T, onChangeValue: (T) -> Unit) -> Unit,
    validate: (T) -> Boolean,
    title: String,
    elementRender: @Composable (T, onEditRequested: () -> Unit, onRemoveRequested: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var editing by remember { mutableStateOf<T?>(null) }
    var modifying by remember { mutableStateOf<T?>(null) }
    modifying?.takeIf { enabled }?.let { element ->
        AlertDialog(
            onDismissRequest = { modifying = null; editing = null },
            title = { Text(title) },
            text = {
                Column {
                    creator(element) { modifying = it }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = validate(element),
                    onClick = {
                        editing?.let {
                            onElementsChange(elements - it + element)
                        } ?: run {
                            onElementsChange(elements + element)
                        }
                        modifying = null
                        editing = null
                    }
                ) { Text(stringResource(Res.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { modifying = null; editing = null }
                ) { Text(stringResource(Res.string.action_cancel)) }
            },
        )
    }

    OutlinedCard(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                enabled = enabled,
                onClick = { modifying = constructor() }
            ) { Icon(Icons.Default.Add, stringResource(Res.string.action_add)) }
        }
        for (element in elements) {
            elementRender(
                element,
                {
                    modifying = element
                    editing = element
                }
            ) { onElementsChange(elements - element) }
        }
    }
}
