package ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Provides a data container for the items that should be displayed inside an
 * [AdaptiveNavigationScaffold]. It's common for all screen sizes, so it can be generalized.
 *
 * @param label The text to display on the item
 * @param icon The icon to display next to the [label].
 */
data class NavigationItem(
    val label: @Composable () -> String,
    val icon: @Composable () -> ImageVector
)
