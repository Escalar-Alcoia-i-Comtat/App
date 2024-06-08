package ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import build.BuildKonfig
import com.russhwolf.settings.set
import database.SettingsKeys
import database.settings
import escalaralcoiaicomtat.composeapp.generated.resources.Res
import escalaralcoiaicomtat.composeapp.generated.resources.action_skip
import escalaralcoiaicomtat.composeapp.generated.resources.action_update
import escalaralcoiaicomtat.composeapp.generated.resources.update_available_dialog_message
import escalaralcoiaicomtat.composeapp.generated.resources.update_available_dialog_message_version
import escalaralcoiaicomtat.composeapp.generated.resources.update_available_dialog_title
import org.jetbrains.compose.resources.stringResource
import utils.format

@Composable
fun UpdateAvailableDialog(
    latestVersion: String?,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.update_available_dialog_title)) },
        text = {
            Text(
                text = latestVersion?.let {
                    stringResource(
                        Res.string.update_available_dialog_message_version
                    ).format(BuildKonfig.VERSION_NAME, it)
                } ?: stringResource(Res.string.update_available_dialog_message)
            )
        },
        confirmButton = {
            TextButton(
                onClick = { /*TODO*/ }
            ) { Text(stringResource(Res.string.action_update)) }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    settings[SettingsKeys.SKIP_VERSION] = latestVersion
                    onDismissRequest()
                }
            ) { Text(stringResource(Res.string.action_skip)) }
        }
    )
}
