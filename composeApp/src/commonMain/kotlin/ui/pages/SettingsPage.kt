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
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import build.BuildKonfig
import cache.File
import cache.ImageCache
import cache.storageProvider
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import maps.KMZHandler
import maps.MapsCache
import resources.MR
import ui.reusable.settings.SettingsCategory
import ui.reusable.settings.SettingsRow
import utils.formatBytes

@Composable
private fun SettingsCacheRow(
    title: StringResource,
    icon: ImageVector,
    directory: File,
    isDeleting: Boolean,
    onDeletingStatusChanged: (Boolean) -> Unit
) {
    var cacheSize by remember { mutableLongStateOf(directory.size() ?: 0L) }

    SettingsRow(
        headline = stringResource(title),
        summary = if (cacheSize >= 0)
            stringResource(MR.strings.settings_storage_size, formatBytes(cacheSize))
        else
            "0KB",
        icon = icon,
        enabled = !isDeleting
    ) {
        onDeletingStatusChanged(true)
        try {
            directory.delete()
            cacheSize = directory.size() ?: 0L
        } finally {
            onDeletingStatusChanged(false)
        }
    }
}

@Composable
fun SettingsPage() {
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

            var deleting by remember { mutableStateOf(false) }

            SettingsCategory(
                text = stringResource(MR.strings.settings_category_storage)
            )
            SettingsCacheRow(
                MR.strings.settings_storage_cache,
                Icons.Outlined.Storage,
                storageProvider.cacheDirectory,
                deleting
            ) { deleting = it }
            SettingsCacheRow(
                MR.strings.settings_storage_images,
                Icons.Outlined.PhotoLibrary,
                ImageCache.imageCacheDirectory,
                deleting
            ) { deleting = it }
            SettingsCacheRow(
                MR.strings.settings_storage_kmz,
                Icons.Outlined.Route,
                KMZHandler.kmzCacheDirectory,
                deleting
            ) { deleting = it }
            MapsCache.tilesCacheDirectory?.let { dir ->
                SettingsCacheRow(
                    MR.strings.settings_storage_maps,
                    Icons.Outlined.Map,
                    dir,
                    deleting
                ) { deleting = it }
            }

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
        }
    }
}
