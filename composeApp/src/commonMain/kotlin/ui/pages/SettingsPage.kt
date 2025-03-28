package ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import build.BuildKonfig
import com.russhwolf.settings.ExperimentalSettingsApi
import escalaralcoiaicomtat.composeapp.generated.resources.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import sync.DataSync
import sync.SyncProcess
import ui.composition.LocalUnitsConfiguration
import ui.model.SettingsModel
import ui.platform.PlatformSettings
import ui.reusable.settings.SettingsCategory
import ui.reusable.settings.SettingsRow
import ui.reusable.settings.SettingsSelector
import utils.IO
import utils.unit.DistanceUnits

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun SettingsPage(model: SettingsModel = viewModel { SettingsModel() }) {
    val unitsConfiguration = LocalUnitsConfiguration.current
    val units by unitsConfiguration.unitsLive.collectAsState(DistanceUnits.METER)

    val isLoading by model.isLoading.collectAsState(false)
    val lastSyncTime by model.lastSyncTime.collectAsState(null)
    val lastSyncCause by model.lastSyncCause.collectAsState(null)
    val syncStatus by model.syncStatus.collectAsState()
    val apiKey by model.apiKey.collectAsState(null)

    SettingsPage(
        isLoading = isLoading,
        lastSyncTime = lastSyncTime,
        lastSyncCause = lastSyncCause,
        syncStatus = syncStatus,
        apiKey = apiKey,
        onLockRequest = model::lock,
        onUnlockRequest = model::unlock,
        distanceUnits = units,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsPage(
    isLoading: Boolean,
    lastSyncTime: Long?,
    lastSyncCause: String?,
    syncStatus: SyncProcess.Status?,
    apiKey: String?,
    onLockRequest: (onLock: () -> Unit) -> Unit,
    onUnlockRequest: (apiKey: String, onUnlock: () -> Unit) -> Unit,
    distanceUnits: DistanceUnits,
) {
    val uriHandler = LocalUriHandler.current
    val unitsConfiguration = LocalUnitsConfiguration.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            PlatformSettings()

            SettingsCategory(
                text = stringResource(Res.string.settings_category_general)
            )
            SettingsSelector(
                headline = stringResource(Res.string.settings_units_distance),
                summary = stringResource(distanceUnits.label),
                icon = Icons.Rounded.Straighten,
                options = DistanceUnits.entries,
                onOptionSelected = {
                    unitsConfiguration.setUnits(it)
                },
                selection = distanceUnits,
                optionsDialogTitle = stringResource(Res.string.settings_units_distance),
                stringConverter = { stringResource(it.label) }
            )

            var showingUnlockDialog by remember { mutableStateOf(false) }
            if (showingUnlockDialog) {
                APIKeyDialog(
                    isLocked = apiKey == null,
                    isLoading = isLoading,
                    onUnlockRequest = {
                        onUnlockRequest(it) { showingUnlockDialog = false }
                    },
                    onLockRequest = {
                        onLockRequest { showingUnlockDialog = false }
                                    },
                    onDismissRequest = { showingUnlockDialog = false }
                )
            }

            Spacer(Modifier.height(16.dp))
            SettingsCategory(
                text = stringResource(Res.string.settings_category_admin)
            )
            SettingsRow(
                headline = stringResource(Res.string.settings_admin_lock_title),
                summary = stringResource(
                    if (apiKey == null)
                        Res.string.settings_admin_lock_locked
                    else
                        Res.string.settings_admin_lock_unlocked
                ),
                icon = if (apiKey == null) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                onClick = { showingUnlockDialog = true },
            )

            Spacer(Modifier.height(16.dp))
            SettingsCategory(
                text = stringResource(Res.string.settings_category_app_info)
            )
            SettingsRow(
                headline = stringResource(Res.string.settings_app_info_last_sync_title),
                summary = if (syncStatus is SyncProcess.Status.RUNNING) {
                    if (syncStatus.isIndeterminate)
                        stringResource(Res.string.settings_app_info_last_sync_running)
                    else
                        stringResource(Res.string.settings_app_info_last_sync_running_progress, syncStatus.progress)
                } else lastSyncTime?.let { time ->
                    val localDateTime = Instant.fromEpochMilliseconds(time)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .let {
                            StringBuilder()
                                .append(it.year)
                                .append('/')
                                .append(it.monthNumber.toString().padStart(2, '0'))
                                .append('/')
                                .append(it.dayOfMonth.toString().padStart(2, '0'))
                                .append(' ')
                                .append(it.hour.toString().padStart(2, '0'))
                                .append(':')
                                .append(it.minute.toString().padStart(2, '0'))
                                .toString()
                        }
                    lastSyncCause
                        ?.let { cause -> DataSync.Cause.entries.find { it.name == cause } }
                        ?.let { cause ->
                            stringResource(
                                Res.string.settings_app_info_last_sync_message,
                                localDateTime,
                                cause.name
                            )
                        }
                        ?: stringResource(
                            Res.string.settings_app_info_last_sync_no_cause,
                            localDateTime
                        )
                } ?: stringResource(Res.string.settings_app_info_last_sync_never),
                icon = Icons.Outlined.Info,
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        DataSync.start(DataSync.Cause.Manual)
                    }
                }
            )
            SettingsRow(
                headline = stringResource(Res.string.settings_app_info_version_code),
                summary = "${BuildKonfig.VERSION_NAME} (${BuildKonfig.VERSION_CODE})",
                icon = Icons.Outlined.Info
            )
            HorizontalDivider()
            SettingsRow(
                headline = stringResource(Res.string.settings_app_info_build_date),
                summary = BuildKonfig.BUILD_DATE,
                icon = Icons.Outlined.Event
            )

            Spacer(Modifier.height(16.dp))
            SettingsCategory(
                text = stringResource(Res.string.settings_category_links)
            )
            SettingsRow(
                headline = stringResource(Res.string.settings_links_status),
                summary = stringResource(Res.string.settings_links_tap),
                icon = Icons.Outlined.Dns
            ) { uriHandler.openUri("https://status.escalaralcoiaicomtat.org/status/services") }
            HorizontalDivider()
            SettingsRow(
                headline = stringResource(Res.string.settings_links_github_app),
                summary = stringResource(Res.string.settings_links_tap),
                icon = Icons.Outlined.Code
            ) { uriHandler.openUri("https://github.com/Escalar-Alcoia-i-Comtat/App") }
            HorizontalDivider()
            SettingsRow(
                headline = stringResource(Res.string.settings_links_github_server),
                summary = stringResource(Res.string.settings_links_tap),
                icon = Icons.Outlined.Dns
            ) { uriHandler.openUri("https://github.com/Escalar-Alcoia-i-Comtat/BackendKotlin") }
            HorizontalDivider()
            SettingsRow(
                headline = stringResource(Res.string.settings_links_crowdin),
                summary = stringResource(Res.string.settings_links_tap),
                icon = Icons.Outlined.Language
            ) { uriHandler.openUri("https://crowdin.com/project/escalar-alcoia-i-comtat") }
        }
    }
}

@Composable
fun APIKeyDialog(
    isLocked: Boolean,
    isLoading: Boolean,
    onUnlockRequest: (apiKey: String) -> Unit,
    onLockRequest: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var apiKey by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismissRequest() },
        title = { Text(stringResource(Res.string.settings_admin_lock_title)) },
        icon = {
            Icon(
                imageVector = if (isLocked) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                contentDescription = stringResource(Res.string.settings_admin_lock_title),
            )
        },
        text = {
            if (isLocked) {
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text(stringResource(Res.string.settings_admin_lock_api_key)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                )
            } else {
                Text(stringResource(Res.string.settings_admin_lock_lock_description))
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isLoading,
                onClick = { if (isLocked) onUnlockRequest(apiKey) else onLockRequest() },
            ) { Text(stringResource(Res.string.action_confirm)) }
        },
    )
}
