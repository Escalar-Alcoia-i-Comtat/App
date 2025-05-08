package org.escalaralcoiaicomtat.app.ui.platform

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SystemSecurityUpdate
import androidx.compose.material.icons.outlined.SystemSecurityUpdateGood
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import escalaralcoiaicomtat.composeapp.generated.resources.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.platform.Updates
import org.escalaralcoiaicomtat.app.ui.reusable.settings.SettingsCategory
import org.escalaralcoiaicomtat.app.ui.reusable.settings.SettingsRow
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
actual fun ColumnScope.PlatformSettings() {
    SettingsCategory(stringResource(Res.string.settings_category_updates))

    var checkingForUpdate by remember { mutableStateOf(false) }
    val updateAvailable by Updates.updateAvailable.collectAsState()
    var updateAvailableAfterCheck by remember { mutableStateOf(false) }

    var performingUpdate by remember { mutableStateOf(false) }
    val downloadProgress by Updates.downloadProgress.collectAsState()
    val updateError by Updates.updateErrorType.collectAsState()

    fun checkForUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            checkingForUpdate = true
            try {
                updateAvailableAfterCheck = Updates.checkForUpdates() == true
            } finally {
                checkingForUpdate = false
            }
        }
    }
    LaunchedEffect(Unit) { checkForUpdates() }

    updateError?.let { error ->
        AlertDialog(
            onDismissRequest = { Updates.updateErrorType.tryEmit(null) },
            title = { Text(stringResource(Res.string.settings_updates_error_title)) },
            text = {
                val errorRes = when (error) {
                    Updates.Error.NO_ASSETS -> Res.string.settings_updates_error_no_assets
                    Updates.Error.RELEASE_NOT_FOUND -> Res.string.settings_updates_error_not_found
                    Updates.Error.UNKNOWN_OS -> Res.string.settings_updates_error_unknown_os
                    Updates.Error.INSTALLER_NOT_FOUND -> Res.string.settings_updates_error_installer_not_found
                }
                Text(
                    text = stringResource(errorRes)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { Updates.updateErrorType.tryEmit(null) }
                ) { Text(stringResource(Res.string.action_close)) }
            }
        )
    }

    if (updateAvailable || updateAvailableAfterCheck) {
        SettingsRow(
            headline = stringResource(Res.string.settings_updates_true_title),
            summary = if (!performingUpdate) {
                stringResource(Res.string.settings_updates_true_summary)
            } else {
                downloadProgress?.let { progress ->
                    when (progress) {
                        in 0f..1f -> stringResource(
                            Res.string.settings_updates_downloading_progress
                        ).format((progress * 100).toInt())
                        Updates.DOWNLOAD_PROGRESS_STORING -> stringResource(Res.string.settings_updates_storing)
                        else -> null
                    }
                } ?: stringResource(Res.string.settings_updates_downloading)
            },
            icon = Icons.Outlined.SystemSecurityUpdate,
            enabled = !performingUpdate
        ) {
            performingUpdate = true
            Updates.requestUpdate()
                ?.invokeOnCompletion { performingUpdate = false }
                ?: run { performingUpdate = false }
        }
    } else {
        SettingsRow(
            headline = stringResource(Res.string.settings_updates_false_title),
            summary = stringResource(
                if (checkingForUpdate)
                    Res.string.settings_updates_checking
                else
                    Res.string.settings_updates_false_summary
            ),
            icon = Icons.Outlined.SystemSecurityUpdateGood,
            enabled = !checkingForUpdate
        ) { checkForUpdates() }
    }
}
