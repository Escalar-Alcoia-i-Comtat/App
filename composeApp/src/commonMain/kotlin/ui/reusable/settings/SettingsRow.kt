package ui.reusable.settings

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SettingsRow(
    headline: String,
    summary: String,
    icon: ImageVector,
    iconContentDescription: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    ListItem(
        headlineContent = { Text(headline) },
        supportingContent = { Text(summary) },
        leadingContent = { Icon(icon, iconContentDescription) },
        modifier = Modifier.clickable(enabled = enabled && onClick != null) { onClick?.invoke() }
    )
}
