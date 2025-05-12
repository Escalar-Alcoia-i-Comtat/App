package org.escalaralcoiaicomtat.app.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.escalaralcoiaicomtat.app.ui.platform.MapComposable
import org.escalaralcoiaicomtat.app.ui.platform.MapViewModel
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MapScreen(
    kmz: Uuid?,
    viewModel: MapViewModel = viewModel { MapViewModel() },
    onBackRequested: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
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
        }
    ) { paddingValues ->
        MapComposable(
            viewModel,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            kmz = kmz,
            blockInteractions = false
        )
    }
}
