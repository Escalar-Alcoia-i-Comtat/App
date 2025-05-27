package org.escalaralcoiaicomtat.app.ui.reusable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ReportButton(onClick: () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
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
