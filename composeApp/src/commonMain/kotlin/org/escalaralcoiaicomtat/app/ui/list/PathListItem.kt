package org.escalaralcoiaicomtat.app.ui.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.data.Path

private const val HighlightColor = 0xffffff00

@Composable
@ExperimentalMaterial3Api
fun PathListItem(
    path: Path,
    /** The blocks of the given [path]. */
    pathBlocks: List<Blocking>,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    var isHighlighted by remember { mutableStateOf(highlight) }
    val borderColor by animateColorAsState(
        targetValue = if (isHighlighted) Color(HighlightColor)
        else MaterialTheme.colorScheme.onBackground
    )

    LaunchedEffect(highlight) {
        if (highlight) isHighlighted = false
    }

    OutlinedCard(
        modifier = modifier,
        border = BorderStroke(1.dp, borderColor),
        colors = if (pathBlocks.isEmpty()) {
            CardDefaults.outlinedCardColors()
        } else {
            pathBlocks.maxBy { it.type.level }.type.cardColors()
        },
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = path.sketchId.toString(),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = path.displayName,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            if (path.grade != null || path.aidGrade != null) {
                Text(
                    text = path.grade(),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
