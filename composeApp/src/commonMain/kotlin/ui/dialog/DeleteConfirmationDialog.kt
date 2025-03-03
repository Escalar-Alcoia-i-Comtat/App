package ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteConfirmationDialog(
    displayName: String,
    onDeleteRequested: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.editor_delete_dialog_title)) },
        text = { Text(stringResource(Res.string.editor_delete_dialog_message, displayName)) },
        confirmButton = {
            TextButton(
                onClick = onDeleteRequested
            ) { Text(stringResource(Res.string.action_confirm)) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) { Text(stringResource(Res.string.action_cancel)) }
        },
    )
}
