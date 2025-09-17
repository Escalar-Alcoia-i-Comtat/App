package org.escalaralcoiaicomtat.app.ui.reusable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ReportButton(onClick: () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
        state = rememberTooltipState(),
        tooltip = {
            PlainTooltip {
                Text(stringResource(Res.string.action_report))
            }
        },
    ) {
        IconButton(
            onClick = onClick,
        ) {
            Icon(Icons.Default.Feedback, stringResource(Res.string.action_report))
        }
    }
}
