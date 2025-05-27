package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FrontHand
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Landslide
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import escalaralcoiaicomtat.composeapp.generated.resources.*
import kotlinx.serialization.Serializable
import org.escalaralcoiaicomtat.app.ui.icons.Rock
import org.escalaralcoiaicomtat.app.ui.icons.Rope
import org.jetbrains.compose.resources.StringResource

@Serializable
enum class BlockingTypes(
    val icon: ImageVector,
    val displayName: StringResource,
    val message: StringResource,
    /**
     * The seriousness of the blocking.
     * - `0`: Just a warning
     * - `1`: Pretty serious
     * - `2`: Climbing is forbidden
     */
    val level: Int,
) {
    DRY(
        Icons.Default.WbSunny,
        Res.string.path_blocking_dry_display_name,
        Res.string.path_blocking_dry_message,
        2,
    ),
    BUILD(
        Icons.Default.Construction,
        Res.string.path_blocking_build_display_name,
        Res.string.path_blocking_build_message,
        2,
    ),
    BIRD(
        Icons.Default.Egg,
        Res.string.path_blocking_bird_display_name,
        Res.string.path_blocking_bird_message,
        2,
    ),
    OLD(
        Icons.Default.Landslide,
        Res.string.path_blocking_old_display_name,
        Res.string.path_blocking_old_message,
        1,
    ),
    PLANTS(
        Icons.Default.Grass,
        Res.string.path_blocking_plants_display_name,
        Res.string.path_blocking_plants_message,
        0,
    ),
    ROPE_LENGTH(
        Icons.Default.Rope,
        Res.string.path_blocking_rope_display_name,
        Res.string.path_blocking_rope_message,
        0,
    ),
    LOOSE_ROCKS(
        Icons.Default.Rock,
        Res.string.path_blocking_loose_rocks_display_name,
        Res.string.path_blocking_loose_rocks_message,
        0,
    ),
    VANDALISM(
        Icons.Default.FrontHand,
        Res.string.path_blocking_vandalism_display_name,
        Res.string.path_blocking_vandalism_message,
        2,
    );

    @Composable
    fun cardColors(): CardColors = CardDefaults.outlinedCardColors(
        containerColor = when (level) {
            0 -> Color(0xffeddb11)
            1 -> MaterialTheme.colorScheme.errorContainer
            2 -> MaterialTheme.colorScheme.errorContainer
            else -> CardDefaults.outlinedCardColors().containerColor
        },
        contentColor = when (level) {
            0 -> Color(0xff494304)
            1 -> MaterialTheme.colorScheme.onErrorContainer
            2 -> MaterialTheme.colorScheme.onErrorContainer
            else -> CardDefaults.outlinedCardColors().contentColor
        },
    )
}
