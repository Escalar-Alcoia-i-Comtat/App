package ui.reusable.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.resources.compose.stringResource
import resources.MR

@Composable
@ExperimentalMaterial3Api
fun <Type> SettingsSelector(
    headline: String,
    summary: String,
    icon: ImageVector,
    options: List<Type>,
    onOptionSelected: (Type) -> Unit,
    selection: Type? = null,
    stringConverter: @Composable (Type) -> String = { it.toString() },
    optionsDialogTitle: String,
    iconContentDescription: String? = null,
    enabled: Boolean = true,
    badgeText: String? = null,
) {
    var showingDialog by remember { mutableStateOf(false) }
    if (showingDialog) {
        AlertDialog(
            onDismissRequest = { showingDialog = false },
            title = { Text(optionsDialogTitle) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(options) { item ->
                        ListItem(
                            headlineContent = { Text(stringConverter(item)) },
                            leadingContent = {
                                Icon(
                                    imageVector = if (selection == item)
                                        Icons.Rounded.RadioButtonChecked
                                    else
                                        Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.clickable { onOptionSelected(item) }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showingDialog = false }) {
                    Text(stringResource(MR.strings.action_close))
                }
            }
        )
    }

    SettingsRow(
        headline,
        summary,
        icon,
        iconContentDescription,
        enabled,
        badgeText
    ) { showingDialog = true }
}
