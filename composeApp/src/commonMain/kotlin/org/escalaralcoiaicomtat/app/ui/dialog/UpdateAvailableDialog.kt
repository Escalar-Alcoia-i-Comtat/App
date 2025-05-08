package org.escalaralcoiaicomtat.app.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import build.BuildKonfig
import com.russhwolf.settings.set
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.platform.Updates
import org.escalaralcoiaicomtat.app.utils.format
import org.jetbrains.compose.resources.stringResource

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
                // TODO: Observe progress
                onClick = { Updates.requestUpdate() }
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
