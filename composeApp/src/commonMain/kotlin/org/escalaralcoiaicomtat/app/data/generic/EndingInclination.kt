package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.ui.graphics.vector.ImageVector
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class EndingInclination(val icon: ImageVector, val stringRes: StringResource) {
    VERTICAL(Icons.Default.North, Res.string.inclination_vertical),
    DIAGONAL(Icons.Default.NorthEast, Res.string.inclination_diagonal),
    HORIZONTAL(Icons.Default.East, Res.string.inclination_horizontal)
}
