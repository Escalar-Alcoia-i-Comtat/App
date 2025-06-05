package org.escalaralcoiaicomtat.app.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.escalaralcoiaicomtat.app.network.response.data.ServerInfoResponseData
import org.escalaralcoiaicomtat.app.sync.SyncManager
import org.escalaralcoiaicomtat.app.sync.SyncProcess
import org.escalaralcoiaicomtat.app.ui.composition.LocalUnitsConfiguration
import org.escalaralcoiaicomtat.app.ui.model.SettingsModel
import org.escalaralcoiaicomtat.app.ui.platform.PlatformSettings
import org.escalaralcoiaicomtat.app.ui.reusable.settings.SettingsCategory
import org.escalaralcoiaicomtat.app.ui.reusable.settings.SettingsRow
import org.escalaralcoiaicomtat.app.ui.reusable.settings.SettingsSelector
import org.escalaralcoiaicomtat.app.utils.unit.DistanceUnits
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun SettingsPage(
    model: SettingsModel = viewModel { SettingsModel() },
    onNavigateToIntroRequested: () -> Unit,
) {
    val unitsConfiguration = LocalUnitsConfiguration.current
    val units by unitsConfiguration.unitsLive.collectAsState(DistanceUnits.METER)

    val isLoading by model.isLoading.collectAsState(false)
    val lastDataSync by model.lastDataSync.collectAsState(null)
    val dataSyncStatus by model.dataSyncStatus.collectAsState()
    val lastBlockingSync by model.lastBlockingSync.collectAsState(null)
    val blockingSyncStatus by model.blockingSyncStatus.collectAsState()
    val serverInfo by model.serverInfo.collectAsState()
    val serverStats by model.serverStats.collectAsState()
    val apiKey by model.apiKey.collectAsState(null)

    LaunchedEffect(Unit) { model.load() }

    SettingsPage(
        isLoading = isLoading,
        lastDataSync = lastDataSync,
        dataSyncStatus = dataSyncStatus,
        lastBlockingSync = lastBlockingSync,
        blockingSyncStatus = blockingSyncStatus,
        apiKey = apiKey,
        serverInfo = serverInfo,
        serverStats = serverStats,
        onLockRequest = model::lock,
        onUnlockRequest = model::unlock,
        distanceUnits = units,
        onIntroRequested = { model.onIntroRequested(onNavigateToIntroRequested) }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsPage(
    isLoading: Boolean,
    lastDataSync: Pair<Instant, SyncProcess.Cause>?,
    dataSyncStatus: SyncProcess.Status?,
    lastBlockingSync: Pair<Instant, SyncProcess.Cause>?,
    blockingSyncStatus: SyncProcess.Status?,
    apiKey: String?,
    serverInfo: ServerInfoResponseData?,
    serverStats: SettingsModel.ServerStats?,
    onLockRequest: (onLock: () -> Unit) -> Unit,
    onUnlockRequest: (apiKey: String, onUnlock: () -> Unit) -> Unit,
    distanceUnits: DistanceUnits,
    onIntroRequested: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val unitsConfiguration = LocalUnitsConfiguration.current

    val lastDataSyncTime = lastDataSync?.first
    val lastDataSyncCause = lastDataSync?.second
    val lastBlockingSyncTime = lastBlockingSync?.first
    val lastBlockingSyncCause = lastBlockingSync?.second

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
                summary = if (dataSyncStatus is SyncProcess.Status.RUNNING) {
                    if (dataSyncStatus.isIndeterminate)
                        stringResource(Res.string.settings_app_info_last_sync_running)
                    else
                        stringResource(
                            Res.string.settings_app_info_last_sync_running_progress,
                            (dataSyncStatus.progress * 100).toInt()
                        )
                } else lastDataSyncTime?.let { time ->
                    val localDateTime = instantToString(time)
                    lastDataSyncCause
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
                icon = Icons.Default.Sync,
                onClick = { SyncManager.runDataSync(SyncProcess.Cause.Manual) }
            )
            HorizontalDivider()
            SettingsRow(
                headline = stringResource(Res.string.settings_app_info_blocking_last_sync_title),
                summary = if (blockingSyncStatus is SyncProcess.Status.RUNNING) {
                    if (blockingSyncStatus.isIndeterminate)
                        stringResource(Res.string.settings_app_info_last_sync_running)
                    else
                        stringResource(
                            Res.string.settings_app_info_last_sync_running_progress,
                            (blockingSyncStatus.progress * 100).toInt()
                        )
                } else lastBlockingSyncTime?.let { time ->
                    val localDateTime = instantToString(time)
                    lastBlockingSyncCause
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
                icon = Icons.Default.Sync,
                onClick = { SyncManager.runBlockingSync(SyncProcess.Cause.Manual) }
            )
            HorizontalDivider()
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
            HorizontalDivider()
            SettingsRow(
                headline = stringResource(Res.string.settings_app_info_intro_title),
                summary = stringResource(Res.string.settings_app_info_intro_message),
                icon = Icons.Outlined.Smartphone,
                onClick = onIntroRequested,
            )

            var showServerInfoStatsDialog by remember { mutableStateOf(false) }
            if (showServerInfoStatsDialog) {
                AlertDialog(
                    onDismissRequest = { showServerInfoStatsDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showServerInfoStatsDialog = false }) {
                            Text(stringResource(Res.string.action_close))
                        }
                    },
                    title = { Text(stringResource(Res.string.settings_server_info_stats_title)) },
                    text = {
                        Column {
                            Text(
                                text = stringResource(
                                    Res.string.settings_server_info_stats_areas,
                                    serverStats?.areasCount ?: 0
                                )
                            )
                            Text(
                                text = stringResource(
                                    Res.string.settings_server_info_stats_zones,
                                    serverStats?.zonesCount ?: 0
                                )
                            )
                            Text(
                                text = stringResource(
                                    Res.string.settings_server_info_stats_sectors,
                                    serverStats?.sectorsCount ?: 0
                                )
                            )
                            Text(
                                text = stringResource(
                                    Res.string.settings_server_info_stats_paths,
                                    serverStats?.pathsCount ?: 0
                                )
                            )
                        }
                    },
                )
            }

            Spacer(Modifier.height(16.dp))
            SettingsCategory(
                text = stringResource(Res.string.settings_category_server_info)
            )
            SettingsRow(
                headline = stringResource(Res.string.settings_server_info_uuid_title),
                summary = serverInfo?.uuid ?: stringResource(Res.string.status_loading),
                icon = Icons.Default.Code,
            )
            HorizontalDivider()
            SettingsRow(
                headline = stringResource(Res.string.settings_server_info_update_title),
                summary = serverInfo?.lastUpdate
                    ?.let(Instant::fromEpochSeconds)
                    ?.let(::instantToString) ?: stringResource(Res.string.status_loading),
                icon = Icons.Default.Event,
            )
            HorizontalDivider()
            SettingsRow(
                headline = stringResource(Res.string.settings_category_server_info),
                summary = stringResource(Res.string.settings_links_tap),
                icon = Icons.AutoMirrored.Filled.ShowChart,
                onClick = { showServerInfoStatsDialog = true },
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
            ) { uriHandler.openUri("https://hosted.weblate.org/engage/escalar-alcoia-i-comtat/") }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.cea),
                        contentDescription = null,
                        modifier = Modifier
                            .size(92.dp)
                            .padding(8.dp),
                    )
                    Column(
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.settings_credits_message),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedButton(
                            onClick = { uriHandler.openUri("https://centrexcursionistalcoi.org/") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        ) { Text(stringResource(Res.string.settings_credits_website)) }
                    }
                }
            }
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

private fun instantToString(instant: Instant): String {
    return instant
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
}
