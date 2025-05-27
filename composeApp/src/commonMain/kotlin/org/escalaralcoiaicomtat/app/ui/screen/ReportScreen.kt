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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Job
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.ui.model.ReportScreenModel
import org.escalaralcoiaicomtat.app.ui.reusable.CircularProgressIndicatorBox
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormField
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormMultipleFilePicker
import org.jetbrains.compose.resources.getString
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
        files = state.files,
        onAddFile = viewModel::addFile,
        onRemoveFile = viewModel::removeFile,
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
    files: List<PlatformFile>,
    onAddFile: (PlatformFile) -> Unit,
    onRemoveFile: (PlatformFile) -> Unit,
    isLoading: Boolean,

    onSendRequested: (suspend () -> Unit) -> Job,
    onBackRequested: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

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
                    IconButton(
                        onClick = {
                            onSendRequested {
                                snackbarHostState.showSnackbar(
                                    getString(Res.string.report_sent)
                                )
                            }.invokeOnCompletion {
                                onBackRequested()
                            }
                        },
                        enabled = !isLoading && message.isNotBlank(),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            stringResource(Res.string.report_send)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
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
                        files = files,
                        onAddFile = onAddFile,
                        onRemoveFile = onRemoveFile,
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
    files: List<PlatformFile>,
    onAddFile: (PlatformFile) -> Unit,
    onRemoveFile: (PlatformFile) -> Unit,
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

        val filesSize = files.sumOf { it.getSize() ?: 0L }
        FormMultipleFilePicker(
            files = files,
            onFilePicked = onAddFile,
            onFileRemoved = onRemoveFile,
            type = PickerType.ImageAndVideo,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.report_attachments),
            enabled = !isLoading,
            error = if (filesSize >= ReportScreenModel.MAX_FILES_SIZE) {
                stringResource(
                    Res.string.report_error_attachments_size,
                    (filesSize / 1024 / 1024).toString() + " MB",
                )
            } else {
                null
            },
        )
    }
}
