package org.escalaralcoiaicomtat.app.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.escalaralcoiaicomtat.app.data.generic.LatLng

@Composable
fun LocationCard(
    icon: ImageVector,
    title: String,
    description: String?,
    point: LatLng,
    modifier: Modifier = Modifier,
    contentDescription: String? = title,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription)
            Column(
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "${point.latitude}, ${point.longitude}",
                    style = MaterialTheme.typography.labelSmall
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
