package ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import ui.composition.LocalNavController
import ui.model.SectorsScreenModel
import ui.navigation.Routes

@Composable
fun SectorsScreen(
    zoneId: Long,
    viewModel: SectorsScreenModel = viewModel { SectorsScreenModel() }
) {
    val navController = LocalNavController.current

    val zone by viewModel.parent.collectAsState()
    val sectors by viewModel.children.collectAsState()

    LaunchedEffect(zoneId) {
        viewModel.load(zoneId) { navController?.navigateUp() }
    }

    DataList(
        parent = zone,
        children = sectors,
        onNavigationRequested = { navController?.navigate(Routes.sector(it.id)) },
        onNavigateUp = { navController?.navigateUp() }
    )
}
