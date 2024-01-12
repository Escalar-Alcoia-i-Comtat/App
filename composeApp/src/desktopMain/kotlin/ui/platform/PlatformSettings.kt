package ui.platform

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
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.Updates
import platform.Updates.Error
import resources.MR
import ui.reusable.settings.SettingsCategory
import ui.reusable.settings.SettingsRow

@Composable
@OptIn(ExperimentalMaterial3Api::class)
actual fun ColumnScope.PlatformSettings() {
    SettingsCategory(stringResource(MR.strings.settings_category_updates))

    var checkingForUpdate by remember { mutableStateOf(false) }
    val updateAvailable by Updates.updateAvailable.collectAsState()
    var updateAvailableAfterCheck by remember { mutableStateOf(false) }

    var performingUpdate by remember { mutableStateOf(false) }
    val downloadProgress by Updates.downloadProgress.collectAsState()
    val updateError by Updates.updateError.collectAsState()

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
            onDismissRequest = { Updates.updateError.tryEmit(null) },
            title = { Text(stringResource(MR.strings.settings_updates_error_title)) },
            text = {
                val errorRes = when (error) {
                    Error.NO_ASSETS -> MR.strings.settings_updates_error_no_assets
                    Error.RELEASE_NOT_FOUND -> MR.strings.settings_updates_error_not_found
                    Error.UNKNOWN_OS -> MR.strings.settings_updates_error_unknown_os
                    Error.INSTALLER_NOT_FOUND -> MR.strings.settings_updates_error_installer_not_found
                }
                Text(
                    text = stringResource(errorRes)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { Updates.updateError.tryEmit(null) }
                ) { Text(stringResource(MR.strings.action_close)) }
            }
        )
    }

    if (updateAvailable || updateAvailableAfterCheck) {
        SettingsRow(
            headline = stringResource(MR.strings.settings_updates_true_title),
            summary = if (!performingUpdate) {
                stringResource(MR.strings.settings_updates_true_summary)
            } else {
                downloadProgress?.let { progress ->
                    stringResource(
                        MR.strings.settings_updates_downloading_progress,
                        (progress * 100).toInt()
                    )
                } ?: stringResource(MR.strings.settings_updates_downloading)
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
            headline = stringResource(MR.strings.settings_updates_false_title),
            summary = stringResource(
                if (checkingForUpdate)
                    MR.strings.settings_updates_checking
                else
                    MR.strings.settings_updates_false_summary
            ),
            icon = Icons.Outlined.SystemSecurityUpdateGood,
            enabled = !checkingForUpdate
        ) { checkForUpdates() }
    }
}
