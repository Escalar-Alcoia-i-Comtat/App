package org.escalaralcoiaicomtat.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.data.DataType
import org.escalaralcoiaicomtat.app.data.DataTypeWithImage
import org.escalaralcoiaicomtat.app.data.DataTypeWithParent
import org.escalaralcoiaicomtat.app.data.DataTypeWithPoint
import org.escalaralcoiaicomtat.app.data.DataTypeWithPoints
import org.escalaralcoiaicomtat.app.data.DataTypes
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.data.editable.EditableExternalTrack
import org.escalaralcoiaicomtat.app.data.editable.EditablePitchInfo
import org.escalaralcoiaicomtat.app.data.editable.EditablePoint
import org.escalaralcoiaicomtat.app.data.generic.AidGrade
import org.escalaralcoiaicomtat.app.data.generic.Builder
import org.escalaralcoiaicomtat.app.data.generic.Ending
import org.escalaralcoiaicomtat.app.data.generic.EndingInclination
import org.escalaralcoiaicomtat.app.data.generic.EndingInfo
import org.escalaralcoiaicomtat.app.data.generic.ExternalTrack
import org.escalaralcoiaicomtat.app.data.generic.LatLng
import org.escalaralcoiaicomtat.app.data.generic.PitchInfo
import org.escalaralcoiaicomtat.app.data.generic.Point
import org.escalaralcoiaicomtat.app.data.generic.SportsGrade
import org.escalaralcoiaicomtat.app.data.generic.SunTime
import org.escalaralcoiaicomtat.app.data.generic.color
import org.escalaralcoiaicomtat.app.exception.StringException
import org.escalaralcoiaicomtat.app.platform.clipEntryOf
import org.escalaralcoiaicomtat.app.ui.dialog.DeleteConfirmationDialog
import org.escalaralcoiaicomtat.app.ui.model.EditorModel
import org.escalaralcoiaicomtat.app.ui.reusable.editor.RichTextEditor
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormDropdown
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormField
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormFilePicker
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormImagePicker
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormListCreator
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormOptionPicker
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormToggleSwitch
import org.escalaralcoiaicomtat.app.ui.state.LaunchedKeyEvent
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun <DT : DataType> EditorScreen(
    dataType: DataTypes<DT>,
    id: Long?, // If null, a new item will be created
    parentId: Long?,
    model: EditorModel<DT> = viewModel { EditorModel(dataType, id, parentId) },
    onBackRequested: () -> Unit,
    afterDelete: () -> Unit,
) {
    val item by model.item.collectAsState()
    val files by model.files.collectAsState()
    val parents by model.parents.collectAsState()
    val isDirty by model.isDirty.collectAsState(false)
    val isLoading by model.isLoading.collectAsState()
    val progress by model.progress.collectAsState()
    val error by model.error.collectAsState()

    LaunchedEffect(Unit) { model.load(onBackRequested) }

    LaunchedKeyEvent { event ->
        if (event.key == Key.Escape && event.type == KeyEventType.KeyUp) {
            onBackRequested()
            true
        } else {
            false
        }
    }

    EditorScreen(
        item = item,
        onUpdateItem = {
            @Suppress("UNCHECKED_CAST")
            model.updateItem(it as DT)
        },
        parents = parents,
        files = files,
        onFilePicked = model::setFile,
        isCreate = id == null,
        isDirty = isDirty,
        isLoading = isLoading,
        progress = progress,
        error = error,
        onClearErrorRequested = model::clearError,
        onSaveRequested = model::save,
        onDeleteRequested = {
            model.delete(afterDelete)
        },
        onBackRequested = onBackRequested,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun <DT : DataType> EditorScreen(
    item: DT?,
    onUpdateItem: (DataType) -> Unit,
    parents: List<DataType>?,
    files: Map<String, PlatformFile>,
    onFilePicked: (key: String, PlatformFile?) -> Unit,
    isCreate: Boolean,
    isDirty: Boolean,
    isLoading: Boolean,
    progress: Float?,
    error: Exception?,
    onClearErrorRequested: () -> Unit,
    onSaveRequested: (onComplete: () -> Unit) -> Unit,
    onDeleteRequested: () -> Unit,
    onBackRequested: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isShowingDeleteDialog by remember { mutableStateOf(false) }
    if (isShowingDeleteDialog) {
        DeleteConfirmationDialog(
            displayName = item?.displayName ?: stringResource(Res.string.editor_loading),
            onDeleteRequested = {
                onDeleteRequested()
                isShowingDeleteDialog = false
            },
            onDismissRequest = { isShowingDeleteDialog = false }
        )
    }

    if (error != null) {
        ErrorDialog(error, onClearErrorRequested)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.displayName ?: stringResource(Res.string.editor_loading)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackRequested
                    ) {
                        Icon(Icons.Default.Close, stringResource(Res.string.editor_close))
                    }
                },
                actions = {
                    if (!isCreate) {
                        IconButton(
                            onClick = { isShowingDeleteDialog = true }
                        ) {
                            Icon(
                                Icons.Default.DeleteForever,
                                stringResource(Res.string.editor_delete)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    if (progress != null && !progress.isNaN()) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            onSaveRequested {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        getString(
                                            if (isCreate) Res.string.editor_created
                                            else Res.string.editor_updated
                                        )
                                    )
                                }
                            }
                        },
                        enabled = isDirty && !isLoading,
                    ) {
                        Text(stringResource(Res.string.action_save))
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { paddingValues ->
        if (item == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                EditorContent(
                    item,
                    onUpdateItem,
                    parents,
                    files,
                    onFilePicked,
                    isCreate,
                    isLoading,
                    snackbarHostState::showSnackbar
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <DT : DataType> EditorContent(
    item: DT,
    onUpdateItem: (DataType) -> Unit,
    parents: List<DataType>?,
    files: Map<String, PlatformFile>,
    onFilePicked: (key: String, PlatformFile?) -> Unit,
    isCreate: Boolean,
    isLoading: Boolean,
    onSnackbarMessageRequested: suspend (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    FormField(
        value = if (isCreate) stringResource(Res.string.editor_id_automatic) else item.id.toString(),
        onValueChange = {},
        label = stringResource(Res.string.editor_id_label),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        readOnly = true,
        enabled = !isLoading,
    )

    if (item is DataTypeWithParent) {
        FormDropdown(
            selection = item.parentId,
            onSelectionChanged = { onUpdateItem(item.copy(parentId = it)) },
            options = parents.orEmpty().map { it.id },
            label = stringResource(Res.string.editor_parent_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            toString = { id ->
                parents.orEmpty().find { it.id == id }?.displayName ?: id.toString()
            },
            enabled = !isLoading,
        )
    }

    FormField(
        value = item.displayName,
        onValueChange = { onUpdateItem(item.copy(displayName = it)) },
        label = stringResource(Res.string.editor_display_name_label),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        enabled = !isLoading,
    )

    if (item is DataTypeWithImage) {
        FormImagePicker(
            file = files[EditorModel.Companion.FILE_KEY_IMAGE],
            onFilePicked = { onFilePicked(EditorModel.Companion.FILE_KEY_IMAGE, it) },
            label = stringResource(Res.string.editor_image_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            fallbackImage = item.imageUrl(),
            enabled = !isLoading,
        )
    }

    if (item is DataTypeWithPoint) {
        LatLngEditor(
            point = item.point,
            onUpdateItem = { onUpdateItem(item.copy(point = it)) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        )
    }

    if (item is DataTypeWithPoints) {
        PointListEditor(
            points = item.points,
            onUpdateItem = { onUpdateItem(item.copy(points = it)) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
    }

    if (item is Zone) {
        FormFilePicker(
            file = files[EditorModel.Companion.FILE_KEY_KMZ],
            onFilePicked = { onFilePicked(EditorModel.Companion.FILE_KEY_KMZ, it) },
            label = stringResource(Res.string.editor_kmz_label),
            type = FileKitType.File(listOf("kmz")),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            fallbackContent = if (item.kmz != null) {
                {
                    Text(
                        text = item.kmz.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 8.dp)
                    )
                }
            } else null,
            enabled = !isLoading,
        )
    }

    if (item is Sector) {
        FormFilePicker(
            file = files[EditorModel.Companion.FILE_KEY_GPX],
            onFilePicked = { onFilePicked(EditorModel.Companion.FILE_KEY_GPX, it) },
            label = stringResource(Res.string.editor_gpx_label),
            type = FileKitType.File(listOf("gpx")),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            fallbackContent = if (item.gpx != null) {
                {
                    Text(
                        text = item.gpx.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 8.dp)
                    )
                }
            } else null,
            enabled = !isLoading,
        )

        ExternalTracksEditor(
            tracks = item.tracks.orEmpty(),
            onUpdateItem = { onUpdateItem(item.copy(tracks = it)) },
            onCopyRequested = {
                scope.launch {
                    clipboard.setClipEntry(
                        clipEntryOf(it)
                    )

                    onSnackbarMessageRequested(getString(Res.string.message_copied))
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )

        FormOptionPicker(
            selection = item.kidsApt,
            onSelectionChanged = { onUpdateItem(item.copy(kidsApt = it)) },
            label = stringResource(Res.string.editor_kids_apt_label),
            options = listOf(true, false),
            toString = { stringResource(if (it) Res.string.editor_yes else Res.string.editor_no) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        )

        FormField(
            value = item.walkingTime?.toString() ?: "",
            onValueChange = { value ->
                if (value.isBlank()) {
                    onUpdateItem(item.copy(walkingTime = null))
                } else {
                    val walkingTime = value.toLongOrNull() ?: return@FormField
                    onUpdateItem(item.copy(walkingTime = walkingTime))
                }
            },
            label = stringResource(Res.string.editor_walking_time_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )

        FormDropdown(
            selection = item.sunTime,
            onSelectionChanged = { onUpdateItem(item.copy(sunTime = it)) },
            label = stringResource(Res.string.editor_sun_time_label),
            options = SunTime.entries,
            toString = { it.label() },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        )
    }
    if (item is Path) {
        HorizontalDivider()

        var sketchId by remember { mutableStateOf(item.sketchId.toString()) }
        FormField(
            value = sketchId,
            onValueChange = { value ->
                sketchId = value
                if (value.isBlank()) {
                    onUpdateItem(item.copy(sketchId = item.sketchId))
                } else {
                    val number = value.toUIntOrNull() ?: item.sketchId
                    onUpdateItem(item.copy(sketchId = number))
                }
            },
            label = stringResource(Res.string.editor_sketch_id_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )

        FormField(
            value = item.height?.toString(),
            onValueChange = { value ->
                if (value.isBlank()) {
                    onUpdateItem(item.copy(height = null))
                } else {
                    val number = value.toUIntOrNull() ?: return@FormField
                    onUpdateItem(item.copy(height = number))
                }
            },
            label = stringResource(Res.string.editor_height_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
            trailingContent = { Text("m") },
        )
        FormDropdown(
            selection = item.grade,
            onSelectionChanged = { onUpdateItem(item.copy(grade = it)) },
            label = stringResource(Res.string.editor_grade_label),
            options = SportsGrade.entries,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
            color = { it.color.current },
            canUnselect = true,
        )
        FormDropdown(
            selection = item.aidGrade,
            onSelectionChanged = { onUpdateItem(item.copy(aidGrade = it)) },
            label = stringResource(Res.string.editor_aid_grade_label),
            options = AidGrade.entries,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
            color = { it.color.current },
            canUnselect = true,
        )
        FormDropdown(
            selection = item.ending,
            onSelectionChanged = { onUpdateItem(item.copy(ending = it)) },
            label = stringResource(Res.string.editor_ending_label),
            options = Ending.entries,
            toString = { stringResource(it.displayName) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )

        HorizontalDivider()

        PitchesEditor(
            list = item.pitches.orEmpty(),
            onUpdateItem = { onUpdateItem(item.copy(pitches = it.takeUnless { it.isEmpty() })) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )

        FormField(
            value = item.stringCount?.toString(),
            onValueChange = { value ->
                if (value.isBlank()) {
                    onUpdateItem(item.copy(stringCount = null))
                } else {
                    val number = value.toUIntOrNull() ?: return@FormField
                    onUpdateItem(item.copy(stringCount = number))
                }
            },
            label = stringResource(Res.string.editor_string_count_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
        FormField(
            value = item.paraboltCount?.toString(),
            onValueChange = { value ->
                if (value.isBlank()) {
                    onUpdateItem(item.copy(paraboltCount = null))
                } else {
                    val number = value.toUIntOrNull() ?: return@FormField
                    onUpdateItem(item.copy(paraboltCount = number))
                }
            },
            label = stringResource(Res.string.editor_parabolt_count_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
        FormField(
            value = item.burilCount?.toString(),
            onValueChange = { value ->
                if (value.isBlank()) {
                    onUpdateItem(item.copy(burilCount = null))
                } else {
                    val number = value.toUIntOrNull() ?: return@FormField
                    onUpdateItem(item.copy(burilCount = number))
                }
            },
            label = stringResource(Res.string.editor_buril_count_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
        FormField(
            value = item.pitonCount?.toString(),
            onValueChange = { value ->
                if (value.isBlank()) {
                    onUpdateItem(item.copy(pitonCount = null))
                } else {
                    val number = value.toUIntOrNull() ?: return@FormField
                    onUpdateItem(item.copy(pitonCount = number))
                }
            },
            label = stringResource(Res.string.editor_piton_count_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
        FormField(
            value = item.spitCount?.toString(),
            onValueChange = { value ->
                if (value.isBlank()) {
                    onUpdateItem(item.copy(spitCount = null))
                } else {
                    val number = value.toUIntOrNull() ?: return@FormField
                    onUpdateItem(item.copy(spitCount = number))
                }
            },
            label = stringResource(Res.string.editor_spit_count_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
        FormField(
            value = item.tensorCount?.toString(),
            onValueChange = { value ->
                if (value.isBlank()) {
                    onUpdateItem(item.copy(tensorCount = null))
                } else {
                    val number = value.toUIntOrNull() ?: return@FormField
                    onUpdateItem(item.copy(tensorCount = number))
                }
            },
            label = stringResource(Res.string.editor_tensor_count_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )

        HorizontalDivider()

        FormToggleSwitch(
            checked = item.nutRequired,
            onCheckedChange = { onUpdateItem(item.copy(nutRequired = it)) },
            label = stringResource(Res.string.editor_nut_required_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
        FormToggleSwitch(
            checked = item.friendRequired,
            onCheckedChange = { onUpdateItem(item.copy(friendRequired = it)) },
            label = stringResource(Res.string.editor_friend_required_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
        FormToggleSwitch(
            checked = item.lanyardRequired,
            onCheckedChange = { onUpdateItem(item.copy(lanyardRequired = it)) },
            label = stringResource(Res.string.editor_lanyard_required_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
        FormToggleSwitch(
            checked = item.nailRequired,
            onCheckedChange = { onUpdateItem(item.copy(nailRequired = it)) },
            label = stringResource(Res.string.editor_nail_required_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
        FormToggleSwitch(
            checked = item.pitonRequired,
            onCheckedChange = { onUpdateItem(item.copy(pitonRequired = it)) },
            label = stringResource(Res.string.editor_piton_required_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )
        FormToggleSwitch(
            checked = item.stapesRequired,
            onCheckedChange = { onUpdateItem(item.copy(stapesRequired = it)) },
            label = stringResource(Res.string.editor_stapes_required_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )

        HorizontalDivider()

        FormToggleSwitch(
            checked = item.showDescription,
            onCheckedChange = { onUpdateItem(item.copy(showDescription = it)) },
            label = stringResource(Res.string.editor_show_description_label),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            enabled = !isLoading,
        )
        Text(
            text = stringResource(Res.string.editor_description_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth(),
        )
        RichTextEditor(
            markdownText = item.description,
            onMarkdownTextChange = { onUpdateItem(item.copy(description = it)) },
            isEnabled = !isLoading
        )

        HorizontalDivider()

        Text(
            text = stringResource(Res.string.editor_builder_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        )
        BuilderEditor(
            builder = item.builder,
            onBuilderChange = { onUpdateItem(item.copy(builder = it.orNull())) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )

        ReBuildersEditor(
            list = item.reBuilders.orEmpty(),
            onUpdateItem = { onUpdateItem(item.copy(reBuilders = it.takeUnless { it.isEmpty() })) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
        )

        // TODO: Path images picker
    }

    Spacer(Modifier.height(32.dp))
}

@Composable
fun ErrorDialog(
    error: Exception,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.editor_error_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                if (error is StringException) {
                    Text(
                        text = error.message ?: "N/A",
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    Text(
                        stringResource(Res.string.editor_error_message_exception)
                    )
                    Text(
                        text = error::class.simpleName ?: "N/A",
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        fontFamily = FontFamily.Monospace,
                    )

                    Text(
                        stringResource(
                            Res.string.editor_error_message_message,
                            error.message ?: "N/A"
                        )
                    )
                    Text(
                        text = error.message ?: "N/A",
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        fontFamily = FontFamily.Monospace,
                    )

                    Text(
                        stringResource(Res.string.editor_error_message_trace)
                    )
                    Text(
                        text = error.stackTraceToString(),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) { Text(stringResource(Res.string.action_close)) }
        },
    )
}

@Composable
private fun LatLngEditor(
    point: LatLng?,
    onUpdateItem: (LatLng?) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        var latitude by remember { mutableStateOf(point?.latitude?.toString() ?: "") }
        var longitude by remember { mutableStateOf(point?.longitude?.toString() ?: "") }

        fun update(lat: String?, lon: String?) {
            latitude = lat ?: latitude
            longitude = lon ?: longitude

            if (latitude.isEmpty() && longitude.isEmpty()) {
                // If both fields are empty, set the point to null
                onUpdateItem(null)
            }

            val newLat = latitude.toDoubleOrNull()
            val newLon = longitude.toDoubleOrNull()

            if (newLat != null && newLon != null) {
                // if there's a valid point set,
                // update the point with the new position
                onUpdateItem(LatLng(newLat, newLon))
            } else {
                // if there's no valid point set
                if (point == null) {
                    // and there was no point set
                    // set the point to null
                    onUpdateItem(null)
                } else {
                    // and there was a point set
                    // update the value to the original one
                    onUpdateItem(point)
                }
            }
        }

        FormField(
            value = latitude,
            onValueChange = { update(it, null) },
            label = stringResource(Res.string.editor_latitude_label),
            modifier = Modifier.weight(1f).padding(end = 4.dp),
            error = stringResource(Res.string.editor_error_coordinate)
                .takeIf { latitude.isNotEmpty() && latitude.toDoubleOrNull() == null },
            enabled = enabled,
        )
        FormField(
            value = longitude,
            onValueChange = { update(null, it) },
            label = stringResource(Res.string.editor_longitude_label),
            modifier = Modifier.weight(1f).padding(start = 4.dp),
            error = stringResource(Res.string.editor_error_coordinate)
                .takeIf { longitude.isNotEmpty() && longitude.toDoubleOrNull() == null },
            enabled = enabled,
        )
    }
}

@Composable
fun BuilderEditor(
    builder: Builder?,
    onBuilderChange: (Builder) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(modifier = modifier) {
        FormField(
            value = builder?.name,
            onValueChange = { onBuilderChange(builder?.copy(name = it) ?: Builder(it)) },
            label = stringResource(Res.string.editor_builder_name_label),
            modifier = Modifier.weight(1f).padding(end = 4.dp),
            enabled = enabled,
        )
        FormField(
            value = builder?.date,
            onValueChange = { onBuilderChange(builder?.copy(date = it) ?: Builder(date = it)) },
            label = stringResource(Res.string.editor_builder_date_label),
            modifier = Modifier.weight(1f).padding(start = 4.dp),
            enabled = enabled,
        )
    }
}

@Composable
private fun PointListEditor(
    points: List<Point>,
    onUpdateItem: (List<Point>) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    FormListCreator(
        elements = points.map(Point::editable),
        onElementsChange = { list -> onUpdateItem(list.map(EditablePoint::build)) },
        constructor = { EditablePoint() },
        validate = EditablePoint::validate,
        enabled = enabled,
        creator = { value, onChange ->
            val (icon, latLng, label) = value
            FormDropdown(
                selection = icon,
                onSelectionChanged = { onChange(value.copy(icon = it)) },
                options = Point.Name.entries,
                label = stringResource(Res.string.editor_external_track_type_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                FormField(
                    value = latLng.latitude,
                    onValueChange = { onChange(value.copy(location = latLng.copy(latitude = it))) },
                    label = stringResource(Res.string.editor_latitude_label),
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    enabled = enabled,
                )
                FormField(
                    value = latLng.longitude,
                    onValueChange = { onChange(value.copy(location = latLng.copy(longitude = it))) },
                    label = stringResource(Res.string.editor_longitude_label),
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    enabled = enabled,
                )
            }
            FormField(
                value = label,
                onValueChange = { onChange(value.copy(label = it)) },
                label = stringResource(Res.string.editor_point_label_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            )
        },
        title = stringResource(Res.string.editor_points_label),
        elementRender = { (icon, latLng, label), _, edit, delete ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon.iconVector,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )

                Column(
                    modifier = Modifier.padding(start = 4.dp).weight(1f),
                ) {
                    Text(
                        text = "${latLng.latitude}, ${latLng.longitude}",
                    )
                    Text(
                        text = label.takeUnless { it.isBlank() } ?: "N/A",
                        fontStyle = if (label.isBlank()) FontStyle.Italic else FontStyle.Normal,
                    )
                }

                IconButton(
                    onClick = edit,
                    enabled = enabled,
                ) { Icon(Icons.Default.Edit, stringResource(Res.string.editor_edit)) }
                IconButton(
                    onClick = delete,
                    enabled = enabled,
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        stringResource(Res.string.editor_delete)
                    )
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun ExternalTracksEditor(
    tracks: List<ExternalTrack>,
    onUpdateItem: (List<ExternalTrack>) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onCopyRequested: (String) -> Unit
) {
    FormListCreator(
        elements = tracks.map { it.editable() },
        onElementsChange = { list -> onUpdateItem(list.map(EditableExternalTrack::build)) },
        constructor = { EditableExternalTrack() },
        validate = EditableExternalTrack::validate,
        enabled = enabled,
        creator = { value, onChange ->
            FormDropdown(
                selection = value.type,
                onSelectionChanged = { onChange(value.copy(type = it)) },
                options = ExternalTrack.Type.entries,
                label = stringResource(Res.string.editor_external_track_type_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            )
            FormField(
                value = value.url,
                onValueChange = { onChange(value.copy(url = it)) },
                label = stringResource(Res.string.editor_external_track_url_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            )
        },
        title = stringResource(Res.string.editor_external_tracks_label),
        elementRender = { (type, url), _, edit, delete ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = type.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )

                Text(
                    text = url,
                    modifier = Modifier.padding(start = 4.dp).weight(1f),
                )

                IconButton(
                    onClick = { onCopyRequested(url) },
                ) { Icon(Icons.Default.ContentCopy, stringResource(Res.string.action_copy)) }
                IconButton(
                    onClick = edit,
                    enabled = enabled,
                ) { Icon(Icons.Default.Edit, stringResource(Res.string.editor_edit)) }
                IconButton(
                    onClick = delete,
                    enabled = enabled,
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        stringResource(Res.string.editor_delete)
                    )
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun ReBuildersEditor(
    list: List<Builder>,
    onUpdateItem: (List<Builder>) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    FormListCreator(
        elements = list,
        onElementsChange = onUpdateItem,
        constructor = { Builder() },
        validate = { true },
        enabled = enabled,
        creator = { value, onChange ->
            BuilderEditor(
                builder = value,
                onBuilderChange = onChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            )
        },
        title = stringResource(Res.string.editor_re_builder_label),
        elementRender = { (name, date), _, edit, delete ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(start = 4.dp).weight(1f),
                ) {
                    Text(
                        text = name ?: "N/A",
                        modifier = Modifier.fillMaxWidth(),
                        fontStyle = if (name == null) FontStyle.Italic else FontStyle.Normal,
                    )
                    Text(
                        text = date ?: "N/A",
                        modifier = Modifier.fillMaxWidth(),
                        fontStyle = if (date == null) FontStyle.Italic else FontStyle.Normal,
                    )
                }

                IconButton(
                    onClick = edit,
                    enabled = enabled,
                ) { Icon(Icons.Default.Edit, stringResource(Res.string.editor_edit)) }
                IconButton(
                    onClick = delete,
                    enabled = enabled,
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        stringResource(Res.string.editor_delete)
                    )
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun PitchesEditor(
    list: List<PitchInfo>,
    onUpdateItem: (List<PitchInfo>) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    FormListCreator(
        elements = list.sortedBy { it.pitch }.map { it.editable() },
        onElementsChange = { onUpdateItem(it.map(EditablePitchInfo::build)) },
        constructor = { EditablePitchInfo() },
        validate = { true },
        enabled = enabled,
        creator = { value, onChange ->
            FormField(
                value = value.pitch,
                onValueChange = { onChange(value.copy(pitch = it)) },
                label = stringResource(Res.string.editor_pitch_info_pitch_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                error = stringResource(Res.string.editor_error_uint)
                    .takeIf { value.pitch.isNotBlank() && value.pitch.toUIntOrNull() == null },
            )
            FormDropdown(
                selection = value.grade,
                onSelectionChanged = { onChange(value.copy(grade = it)) },
                options = (SportsGrade.entries),
                label = stringResource(Res.string.editor_pitch_info_grade_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                canUnselect = true,
            )
            FormDropdown(
                selection = value.aidGrade,
                onSelectionChanged = { onChange(value.copy(aidGrade = it)) },
                options = (AidGrade.entries),
                label = stringResource(Res.string.editor_pitch_info_aid_grade_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                canUnselect = true,
            )
            FormField(
                value = value.height,
                onValueChange = { onChange(value.copy(height = it)) },
                label = stringResource(Res.string.editor_pitch_info_height_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                error = stringResource(Res.string.editor_error_uint)
                    .takeIf { value.height.isNotBlank() && value.height.toUIntOrNull() == null },
            )
            FormDropdown(
                selection = value.ending,
                onSelectionChanged = { onChange(value.copy(ending = it)) },
                options = Ending.entries,
                label = stringResource(Res.string.editor_pitch_info_ending_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                toString = { stringResource(it.displayName) },
            )
            FormDropdown(
                selection = value.info,
                onSelectionChanged = { onChange(value.copy(info = it)) },
                options = EndingInfo.entries,
                label = stringResource(Res.string.editor_pitch_info_ending_info_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                icon = { it.icon },
                toString = { stringResource(it.stringRes) },
            )
            FormDropdown(
                selection = value.inclination,
                onSelectionChanged = { onChange(value.copy(inclination = it)) },
                options = EndingInclination.entries,
                label = stringResource(Res.string.editor_pitch_info_ending_inclination_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                icon = { it.icon },
                toString = { stringResource(it.stringRes) },
            )
        },
        title = stringResource(Res.string.editor_pitch_info_label),
        elementRender = { pitch, isLast, edit, delete ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(start = 4.dp).weight(1f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Badge {
                            Text("L${pitch.pitch}")
                        }
                        Text(
                            text = pitch.grade?.toString() ?: "N/A",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontStyle = if (pitch.grade == null) FontStyle.Italic else FontStyle.Normal,
                            color = pitch.grade?.color?.current ?: LocalContentColor.current,
                        )
                        Text(
                            text = pitch.aidGrade?.toString() ?: "N/A",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontStyle = if (pitch.aidGrade == null) FontStyle.Italic else FontStyle.Normal,
                            color = pitch.aidGrade?.color?.current ?: LocalContentColor.current,
                        )
                        Text(
                            text = pitch.height.takeUnless { it.isBlank() }?.let { "$it m" } ?: "N/A",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontStyle = if (pitch.height.isBlank()) FontStyle.Italic else FontStyle.Normal,
                        )
                        Text(
                            text = pitch.ending?.let { stringResource(it.displayName) } ?: "N/A",
                            modifier = Modifier.weight(1f),
                            fontStyle = if (pitch.ending == null) FontStyle.Italic else FontStyle.Normal,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(pitch.info?.icon ?: Icons.Default.Remove, null)
                        Text(
                            text = pitch.info?.let { stringResource(it.stringRes) } ?: "N/A",
                            modifier = Modifier.weight(1f),
                            fontStyle = if (pitch.info == null) FontStyle.Italic else FontStyle.Normal,
                        )
                        Image(pitch.inclination?.icon ?: Icons.Default.Remove, null)
                        Text(
                            text = pitch.inclination?.let { stringResource(it.stringRes) } ?: "N/A",
                            modifier = Modifier.weight(1f),
                            fontStyle = if (pitch.inclination == null) FontStyle.Italic else FontStyle.Normal,
                        )
                    }
                }

                IconButton(
                    onClick = edit,
                    enabled = enabled,
                ) { Icon(Icons.Default.Edit, stringResource(Res.string.editor_edit)) }
                IconButton(
                    onClick = delete,
                    enabled = enabled,
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        stringResource(Res.string.editor_delete)
                    )
                }
            }
            if (!isLast) HorizontalDivider()
        },
        modifier = modifier,
    )
}
