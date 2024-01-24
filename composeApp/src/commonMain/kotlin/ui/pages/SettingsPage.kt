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
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import build.BuildKonfig
import cache.File
import cache.ImageCache
import com.russhwolf.settings.ExperimentalSettingsApi
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import maps.KMZHandler
import maps.MapsCache
import resources.MR
import ui.composition.LocalUnitsConfiguration
import ui.platform.PlatformSettings
import ui.reusable.settings.SettingsCategory
import ui.reusable.settings.SettingsRow
import ui.reusable.settings.SettingsSelector
import utils.formatBytes
import utils.unit.DistanceUnits

@Composable
@ExperimentalMaterial3Api
private fun SettingsCacheRow(
    title: StringResource,
    icon: ImageVector,
    directory: File,
    isDeleting: Boolean,
    onDeletingStatusChanged: (Boolean) -> Unit
) {
    var cacheSize by remember { mutableLongStateOf(directory.size() ?: 0L) }
    var showingDialog by remember { mutableStateOf(false) }

    if (showingDialog) {
        AlertDialog(
            onDismissRequest = {
                // Do not allow dismiss while deleting
                if (!isDeleting) showingDialog = false
            },
            title = { Text(stringResource(MR.strings.settings_storage_dialog_title)) },
            text = { Text(stringResource(MR.strings.settings_storage_dialog_message)) },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        onDeletingStatusChanged(true)
                        try {
                            directory.delete()
                            cacheSize = directory.size() ?: 0L
                        } finally {
                            onDeletingStatusChanged(false)
                            showingDialog = false
                        }
                    }
                ) { Text(stringResource(MR.strings.action_clear)) }
            },
            dismissButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = { showingDialog = false }
                ) { Text(stringResource(MR.strings.action_cancel)) }
            }
        )
    }

    SettingsRow(
        headline = stringResource(title),
        summary = if (cacheSize >= 0)
            stringResource(MR.strings.settings_storage_size, formatBytes(cacheSize))
        else
            "0KB",
        icon = icon,
        enabled = !isDeleting
    ) { showingDialog = true }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSettingsApi::class)
fun SettingsPage() {
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

            val units by unitsConfiguration.unitsLive.collectAsState(DistanceUnits.METER)

            SettingsCategory(
                text = stringResource(MR.strings.settings_category_general)
            )
            SettingsSelector(
                headline = stringResource(MR.strings.settings_units_distance),
                summary = stringResource(units.label),
                icon = Icons.Rounded.Straighten,
                options = DistanceUnits.entries,
                onOptionSelected = {
                    unitsConfiguration.setUnits(it)
                },
                selection = units,
                optionsDialogTitle = stringResource(MR.strings.settings_units_distance),
                stringConverter = { stringResource(it.label) }
            )

            var deleting by remember { mutableStateOf(false) }

            SettingsCategory(
                text = stringResource(MR.strings.settings_category_storage)
            )
            SettingsCacheRow(
                MR.strings.settings_storage_images,
                Icons.Outlined.PhotoLibrary,
                ImageCache.imageCacheDirectory,
                deleting
            ) { deleting = it }
            Divider()
            SettingsCacheRow(
                MR.strings.settings_storage_kmz,
                Icons.Outlined.Route,
                KMZHandler.kmzCacheDirectory,
                deleting
            ) { deleting = it }
            MapsCache.tilesCacheDirectory?.let { dir ->
                Divider()
                SettingsCacheRow(
                    MR.strings.settings_storage_maps,
                    Icons.Outlined.Map,
                    dir,
                    deleting
                ) { deleting = it }
            }

            Spacer(Modifier.height(16.dp))
            SettingsCategory(
                text = stringResource(MR.strings.settings_category_app_info)
            )
            BuildKonfig.VERSION_CODE?.let { versionCode ->
                SettingsRow(
                    headline = stringResource(MR.strings.settings_app_info_version_code),
                    summary = "${BuildKonfig.VERSION_NAME} ($versionCode)",
                    icon = Icons.Outlined.Info
                )
            } ?: run {
                SettingsRow(
                    headline = stringResource(MR.strings.settings_app_info_version),
                    summary = BuildKonfig.VERSION_NAME,
                    icon = Icons.Outlined.Info
                )
            }
            Divider()
            SettingsRow(
                headline = stringResource(MR.strings.settings_app_info_build_date),
                summary = BuildKonfig.BUILD_DATE,
                icon = Icons.Outlined.Event
            )

            Spacer(Modifier.height(16.dp))
            SettingsCategory(
                text = stringResource(MR.strings.settings_category_links)
            )
            SettingsRow(
                headline = stringResource(MR.strings.settings_links_status),
                summary = stringResource(MR.strings.settings_links_tap),
                icon = Icons.Outlined.Dns
            ) { uriHandler.openUri("https://status.arnyminerz.com/status/escalaralcoiaicomtat") }
            Divider()
            SettingsRow(
                headline = stringResource(MR.strings.settings_links_github_app),
                summary = stringResource(MR.strings.settings_links_tap),
                icon = Icons.Outlined.Code
            ) { uriHandler.openUri("https://github.com/Escalar-Alcoia-i-Comtat/App") }
            Divider()
            SettingsRow(
                headline = stringResource(MR.strings.settings_links_github_server),
                summary = stringResource(MR.strings.settings_links_tap),
                icon = Icons.Outlined.Dns
            ) { uriHandler.openUri("https://github.com/Escalar-Alcoia-i-Comtat/BackendKotlin") }
            Divider()
            SettingsRow(
                headline = stringResource(MR.strings.settings_links_crowdin),
                summary = stringResource(MR.strings.settings_links_tap),
                icon = Icons.Outlined.Dns,
                badgeText = stringResource(MR.strings.settings_soon)
            )
        }
    }
}
