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
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import build.BuildKonfig
import com.russhwolf.settings.ExperimentalSettingsApi
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ui.composition.LocalUnitsConfiguration
import ui.platform.PlatformSettings
import ui.reusable.settings.SettingsCategory
import ui.reusable.settings.SettingsRow
import ui.reusable.settings.SettingsSelector
import utils.unit.DistanceUnits

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
                text = stringResource(Res.string.settings_category_general)
            )
            SettingsSelector(
                headline = stringResource(Res.string.settings_units_distance),
                summary = stringResource(units.label),
                icon = Icons.Rounded.Straighten,
                options = DistanceUnits.entries,
                onOptionSelected = {
                    unitsConfiguration.setUnits(it)
                },
                selection = units,
                optionsDialogTitle = stringResource(Res.string.settings_units_distance),
                stringConverter = { stringResource(it.label) }
            )

            Spacer(Modifier.height(16.dp))
            SettingsCategory(
                text = stringResource(Res.string.settings_category_app_info)
            )
            BuildKonfig.VERSION_CODE?.let { versionCode ->
                SettingsRow(
                    headline = stringResource(Res.string.settings_app_info_version_code),
                    summary = "${BuildKonfig.VERSION_NAME} ($versionCode)",
                    icon = Icons.Outlined.Info
                )
            } ?: run {
                SettingsRow(
                    headline = stringResource(Res.string.settings_app_info_version),
                    summary = BuildKonfig.VERSION_NAME,
                    icon = Icons.Outlined.Info
                )
            }
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
