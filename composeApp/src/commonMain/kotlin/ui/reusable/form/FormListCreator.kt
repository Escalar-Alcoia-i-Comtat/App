package ui.reusable.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
    elementRender: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var creating by remember { mutableStateOf<T?>(null) }
    creating?.let { mod ->
        AlertDialog(
            onDismissRequest = { creating = null },
            title = { Text(title) },
            text = {
                Column {
                    creator(mod) { creating = mod }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = validate(mod),
                    onClick = { onElementsChange(elements + mod) }
                ) { Text(stringResource(Res.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { creating = null }
                ) { Text(stringResource(Res.string.action_cancel)) }
            },
        )
    }

    OutlinedCard(
        modifier = modifier,
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { creating = constructor() }
            ) { Icon(Icons.Default.Add, stringResource(Res.string.action_add)) }
        }
        for (element in elements) elementRender(element)
    }
}
