package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.ui.graphics.vector.ImageVector
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.escalaralcoiaicomtat.app.ui.icons.Rope
import org.jetbrains.compose.resources.StringResource

enum class EndingInfo(val icon: ImageVector, val stringRes: StringResource) {
    RAPPEL(Icons.Default.Rope, Res.string.ending_rappel),
    EQUIPPED(Icons.Default.Link, Res.string.ending_equipped),
    CLEAN(Icons.Default.RadioButtonUnchecked, Res.string.ending_clean)
}
