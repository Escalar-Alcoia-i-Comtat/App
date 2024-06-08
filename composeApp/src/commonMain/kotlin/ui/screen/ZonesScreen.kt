package ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import ui.composition.LocalNavController
import ui.model.ZonesScreenModel
import ui.navigation.Routes

@Composable
fun ZonesScreen(
    areaId: Long,
    viewModel: ZonesScreenModel = viewModel()
) {
    val navController = LocalNavController.current

    val area by viewModel.parent.collectAsState()
    val zones by viewModel.children.collectAsState()

    LaunchedEffect(areaId) {
        viewModel.load(areaId) { navController?.navigateUp() }
    }

    DataList(
        parent = area,
        children = zones,
        onNavigationRequested = { navController?.navigate(Routes.zone(it.id)) },
        onNavigateUp = { navController?.navigateUp() }
    )
}
