package org.escalaralcoiaicomtat.app.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.ui.model.ReportScreenModel
import org.escalaralcoiaicomtat.app.ui.reusable.CircularProgressIndicatorBox
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormField
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReportScreen(
    sectorId: Long?,
    pathId: Long?,
    onBackRequested: () -> Unit,
    viewModel: ReportScreenModel = viewModel { ReportScreenModel(sectorId, pathId) },
) {
    val sector by viewModel.sector.collectAsState(null)
    val path by viewModel.path.collectAsState(null)
    val isLoading by viewModel.isLoading.collectAsState()
    val state by viewModel.state.collectAsState()

    ReportScreen(
        sector,
        path,
        name = state.name,
        onNameChange = viewModel::onNameChange,
        email = state.email,
        onEmailChange = viewModel::onEmailChange,
        message = state.message,
        onMessageChange = viewModel::onMessageChange,
        isLoading = isLoading,
        onSendRequested = viewModel::send,
        onBackRequested = onBackRequested,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ReportScreen(
    sector: Sector?,
    path: Path?,

    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
    isLoading: Boolean,

    onSendRequested: () -> Unit,
    onBackRequested: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.report_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackRequested, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = onSendRequested, enabled = !isLoading) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            stringResource(Res.string.report_send)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = sector to path,
            modifier = Modifier.padding(paddingValues),
        ) { data ->
            if (data.first == null && data.second == null) {
                CircularProgressIndicatorBox()
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ReportScreenContent(
                        sector = data.first,
                        path = data.second,
                        name = name,
                        onNameChange = onNameChange,
                        email = email,
                        onEmailChange = onEmailChange,
                        message = message,
                        onMessageChange = onMessageChange,
                        isLoading = isLoading,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportScreenContent(
    sector: Sector?,
    path: Path?,

    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
    ) {
        FormField(
            value = sector?.displayName ?: path?.displayName ?: "N/A",
            onValueChange = {},
            label = stringResource(Res.string.report_element),
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        )
        FormField(
            value = name,
            onValueChange = onNameChange,
            label = stringResource(Res.string.report_name),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        )
        FormField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(Res.string.report_email),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        )
        FormField(
            value = message,
            onValueChange = onMessageChange,
            label = stringResource(Res.string.report_message),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = false,
        )
    }
}
