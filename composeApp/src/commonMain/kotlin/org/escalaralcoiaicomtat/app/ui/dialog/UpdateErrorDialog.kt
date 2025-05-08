package org.escalaralcoiaicomtat.app.ui.dialog

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun UpdateErrorDialog(
    errorMessage: String,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.update_error_title)) },
        text = {
            SelectionContainer { Text(errorMessage) }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) { Text(stringResource(Res.string.update_action_dismiss)) }
        },
    )
}
