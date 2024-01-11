package ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import build.BuildKonfig

@Composable
fun SettingsPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // TODO: Header for storage configuration
            // TODO: Fields for clearing specific cache pieces

            // TODO: Header for app information
            ListItem(
                headlineContent = { Text("Version Name") },
                supportingContent = { Text(BuildKonfig.VERSION_NAME) }
            )
            BuildKonfig.VERSION_CODE?.let { versionCode ->
                ListItem(
                    headlineContent = { Text("Version Code") },
                    supportingContent = { Text(versionCode.toString()) }
                )
            }
            ListItem(
                headlineContent = { Text("Build Date") },
                supportingContent = { Text(BuildKonfig.BUILD_DATE) }
            )
        }
    }
}
