package ui.screen

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import data.DataType
import data.DataTypeWithImage
import data.DataTypeWithParent
import data.DataTypeWithPoint
import data.DataTypeWithPoints
import data.DataTypes
import data.Path
import data.Sector
import data.Zone
import data.editable.EditableExternalTrack
import data.editable.EditablePitchInfo
import data.editable.EditablePoint
import data.generic.ArtificialGrade
import data.generic.Builder
import data.generic.Ending
import data.generic.EndingInclination
import data.generic.EndingInfo
import data.generic.ExternalTrack
import data.generic.LatLng
import data.generic.PitchInfo
import data.generic.Point
import data.generic.SportsGrade
import data.generic.SunTime
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import platform.BackHandler
import ui.dialog.DeleteConfirmationDialog
import ui.model.EditorModel
import ui.model.EditorModel.Companion.FILE_KEY_GPX
import ui.model.EditorModel.Companion.FILE_KEY_IMAGE
import ui.model.EditorModel.Companion.FILE_KEY_KMZ
import ui.reusable.editor.RichTextStyleRow
import ui.reusable.form.FormDropdown
import ui.reusable.form.FormField
import ui.reusable.form.FormFilePicker
import ui.reusable.form.FormImagePicker
import ui.reusable.form.FormListCreator
import ui.reusable.form.FormOptionPicker
import ui.reusable.form.FormToggleSwitch
import ui.state.LaunchedKeyEvent

@Composable
fun <DT : DataType> EditorScreen(
    dataType: DataTypes<DT>,
    id: Long?, // If null, a new item will be created
    parentId: Long?,
    model: EditorModel<DT> = viewModel { EditorModel(dataType, id, parentId) },
    onBackRequested: () -> Unit
) {
    val item by model.item.collectAsState()
    val files by model.files.collectAsState()
    val parents by model.parents.collectAsState()
    val isDirty by model.isDirty.collectAsState(false)
    val isLoading by model.isLoading.collectAsState()
    val progress by model.progress.collectAsState()
    val error by model.error.collectAsState()

    LaunchedEffect(Unit) { model.load(onBackRequested) }

    BackHandler { onBackRequested() }

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
            model.delete(onBackRequested)
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
                    if (progress != null) {
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
    val clipboardManager = LocalClipboardManager.current

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
            file = files[FILE_KEY_IMAGE],
            onFilePicked = { onFilePicked(FILE_KEY_IMAGE, it) },
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
            file = files[FILE_KEY_KMZ],
            onFilePicked = { onFilePicked(FILE_KEY_KMZ, it) },
            label = stringResource(Res.string.editor_kmz_label),
            type = PickerType.File(listOf("kmz")),
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
            file = files[FILE_KEY_GPX],
            onFilePicked = { onFilePicked(FILE_KEY_GPX, it) },
            label = stringResource(Res.string.editor_gpx_label),
            type = PickerType.File(listOf("gpx")),
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
                clipboardManager.setText(buildAnnotatedString { append(it) })
                scope.launch {
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
                    val number = value.toUIntOrNull() ?: return@FormField
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
            onSelectionChanged = { onUpdateItem(item.copy(gradeValue = it.name)) },
            label = stringResource(Res.string.editor_grade_label),
            options = SportsGrade.entries,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            enabled = !isLoading,
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

        val state = rememberRichTextState()
        LaunchedEffect(Unit) {
            item.description?.let(state::setMarkdown)
            snapshotFlow { state.annotatedString }
                .collect {
                    onUpdateItem(
                        item.copy(description = state.toMarkdown().takeUnless { it.isBlank() })
                    )
                }
        }
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
        RichTextStyleRow(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        )
        OutlinedRichTextEditor(
            state = state,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
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
            val (newLat, newLon) = if (lat != null) {
                latitude = lat
                lat.toDoubleOrNull() to point?.longitude
            } else if (lon != null) {
                longitude = lon
                point?.latitude to lon.toDoubleOrNull()
            } else {
                // Won't happen, but must be handled
                point?.latitude to point?.longitude
            }
            if (newLat != null && newLon != null) {
                onUpdateItem(LatLng(newLat, newLon))
            } else {
                onUpdateItem(null)
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
        elementRender = { (icon, latLng, label), edit, delete ->
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
        elementRender = { (type, url), edit, delete ->
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
        creator = { value, onChange ->
            BuilderEditor(
                builder = value,
                onBuilderChange = onChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            )
        },
        title = stringResource(Res.string.editor_re_builder_label),
        elementRender = { (name, date), edit, delete ->
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
    FormListCreator<EditablePitchInfo>(
        elements = list.map { it.editable() },
        onElementsChange = { onUpdateItem(it.map(EditablePitchInfo::build)) },
        constructor = { EditablePitchInfo() },
        validate = { true },
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
                options = (SportsGrade.entries + ArtificialGrade.entries),
                label = stringResource(Res.string.editor_pitch_info_grade_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
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
            )
            FormDropdown(
                selection = value.info,
                onSelectionChanged = { onChange(value.copy(info = it)) },
                options = EndingInfo.entries,
                label = stringResource(Res.string.editor_pitch_info_ending_info_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            )
            FormDropdown(
                selection = value.inclination,
                onSelectionChanged = { onChange(value.copy(inclination = it)) },
                options = EndingInclination.entries,
                label = stringResource(Res.string.editor_pitch_info_ending_inclination_label),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            )
        },
        title = stringResource(Res.string.editor_pitch_info_label),
        elementRender = { pitch, edit, delete ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(start = 4.dp).weight(1f),
                ) {
                    Text(
                        text = pitch.pitch,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = pitch.grade?.toString() ?: "N/A",
                        modifier = Modifier.fillMaxWidth(),
                        fontStyle = if (pitch.grade == null) FontStyle.Italic else FontStyle.Normal,
                    )
                    Text(
                        text = pitch.height.takeUnless { it.isBlank() } ?: "N/A",
                        modifier = Modifier.fillMaxWidth(),
                        fontStyle = if (pitch.height.isBlank()) FontStyle.Italic else FontStyle.Normal,
                    )
                    Text(
                        text = pitch.ending?.displayName?.let { stringResource(it) } ?: "N/A",
                        modifier = Modifier.fillMaxWidth(),
                        fontStyle = if (pitch.ending == null) FontStyle.Italic else FontStyle.Normal,
                    )
                    Text(
                        text = pitch.info?.toString() ?: "N/A",
                        modifier = Modifier.fillMaxWidth(),
                        fontStyle = if (pitch.info == null) FontStyle.Italic else FontStyle.Normal,
                    )
                    Text(
                        text = pitch.inclination?.toString() ?: "N/A",
                        modifier = Modifier.fillMaxWidth(),
                        fontStyle = if (pitch.inclination == null) FontStyle.Italic else FontStyle.Normal,
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
