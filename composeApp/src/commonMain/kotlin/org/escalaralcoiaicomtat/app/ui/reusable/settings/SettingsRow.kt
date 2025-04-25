package org.escalaralcoiaicomtat.app.ui.reusable.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
@ExperimentalMaterial3Api
fun SettingsRow(
    headline: String,
    summary: String,
    icon: ImageVector,
    iconContentDescription: String? = null,
    enabled: Boolean = true,
    badgeText: String? = null,
    onClick: (() -> Unit)? = null
) {
    ListItem(
        headlineContent = {
            badgeText?.let { badge ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(headline)

                    Badge(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            } ?: Text(headline)
        },
        supportingContent = { Text(summary) },
        leadingContent = { Icon(icon, iconContentDescription) },
        modifier = Modifier.clickable(enabled = enabled && onClick != null) { onClick?.invoke() }
    )
}
