package org.escalaralcoiaicomtat.app.ui.navigation

import androidx.compose.material.symbols.Explore24DpW100G0
import androidx.compose.material.symbols.Explore24DpW100G200
import androidx.compose.material.symbols.Settings24DpW100G0
import androidx.compose.material.symbols.Settings24DpW100G200
import androidx.compose.material.symbols.Symbols
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.ui.tooling.preview.Preview

data class NavigationBarItem(
    val icon: @Composable () -> ImageVector,
    val selectedIcon: @Composable () -> ImageVector,
    val label: @Composable () -> String,
)

@Composable
fun NavigationBar(items: List<NavigationBarItem>) {

}

@Preview
@Composable
fun NavigationBar_Preview() {
    NavigationBar(
        items = listOf(
            NavigationBarItem(
                icon = { Symbols.Explore24DpW100G0 },
                selectedIcon = { Symbols.Explore24DpW100G200 },
                label = { "Explore" },
            ),
            NavigationBarItem(
                icon = { Symbols.Settings24DpW100G0 },
                selectedIcon = { Symbols.Settings24DpW100G200 },
                label = { "Settings" },
            ),
        ),
    )
}
