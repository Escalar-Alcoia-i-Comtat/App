package org.escalaralcoiaicomtat.app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.escalaralcoiaicomtat.app.ui.platform.MapComposable
import org.escalaralcoiaicomtat.app.ui.platform.MapViewModel
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
fun MapScreen(
    kmz: Uuid?,
    viewModel: MapViewModel = viewModel { MapViewModel() },
    onBackRequested: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.map_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackRequested
                    ) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            stringResource(Res.string.action_back),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            val windowSizeClass = calculateWindowSizeClass()
            if (windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Expanded) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp).zIndex(10f)
                ) {
                    SegmentedButton(
                        selected = false,
                        onClick = { viewModel.zoomOut() },
                        shape = SegmentedButtonDefaults.itemShape(0, 2),
                        label = { Icon(Icons.Default.Remove, null) }
                    )
                    SegmentedButton(
                        selected = false,
                        onClick = { viewModel.zoomIn() },
                        shape = SegmentedButtonDefaults.itemShape(1, 2),
                        label = { Icon(Icons.Default.Add, null) }
                    )
                }
            }

            MapComposable(
                viewModel,
                modifier = Modifier.fillMaxSize(),
                kmz = kmz,
                blockInteractions = false
            )
        }
    }
}
