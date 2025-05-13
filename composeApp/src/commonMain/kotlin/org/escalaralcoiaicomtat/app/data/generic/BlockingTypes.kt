package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Landslide
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import escalaralcoiaicomtat.composeapp.generated.resources.*
import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.ui.icons.Rope
import org.jetbrains.compose.resources.StringResource

@Serializable
enum class BlockingTypes(
    val icon: ImageVector,
    val message: StringResource,
) {
    DRY(Icons.Default.WbSunny, Res.string.path_blocking_dry_message),
    BUILD(Icons.Default.Construction, Res.string.path_blocking_build_message),
    BIRD(Icons.Default.Egg, Res.string.path_blocking_bird_message),
    OLD(Icons.Default.Landslide, Res.string.path_blocking_old_message),
    PLANTS(Icons.Default.Grass, Res.string.path_blocking_plants_message),
    ROPE_LENGTH(Icons.Default.Rope, Res.string.path_blocking_rope_message)
}
