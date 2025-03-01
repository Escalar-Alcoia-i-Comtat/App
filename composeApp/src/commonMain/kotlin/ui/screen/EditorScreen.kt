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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import data.DataType
import data.DataTypeWithImage
import data.DataTypeWithParent
import data.DataTypeWithPoint
import data.DataTypes
import data.Path
import data.Sector
import data.Zone
import data.editable.EditableExternalTrack
import data.generic.Ending
import data.generic.ExternalTrack
import data.generic.LatLng
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
    model: EditorModel<DT> = viewModel { EditorModel(dataType, id) },
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
    onBackRequested: () -> Unit
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
                EditorContent(item, onUpdateItem, parents, files, onFilePicked, isCreate, isLoading)
            }
        }
    }
}

@Composable
private fun <DT : DataType> EditorContent(
    item: DT,
    onUpdateItem: (DataType) -> Unit,
    parents: List<DataType>?,
    files: Map<String, PlatformFile>,
    onFilePicked: (key: String, PlatformFile?) -> Unit,
    isCreate: Boolean,
    isLoading: Boolean,
) {
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
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            var latitude by remember { mutableStateOf(item.point?.latitude?.toString() ?: "") }
            var longitude by remember { mutableStateOf(item.point?.longitude?.toString() ?: "") }

            fun update(lat: String?, lon: String?) {
                val (newLat, newLon) = if (lat != null) {
                    latitude = lat
                    lat.toDoubleOrNull() to item.point?.longitude
                } else if (lon != null) {
                    longitude = lon
                    item.point?.latitude to lon.toDoubleOrNull()
                } else {
                    // Won't happen, but must be handled
                    item.point?.latitude to item.point?.longitude
                }
                if (newLat != null && newLon != null) {
                    onUpdateItem(item.copy(point = LatLng(newLat, newLon)))
                } else {
                    onUpdateItem(item.copy(point = null))
                }
            }

            FormField(
                value = latitude,
                onValueChange = { update(it, null) },
                label = stringResource(Res.string.editor_latitude_label),
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                error = stringResource(Res.string.editor_error_coordinate)
                    .takeIf { latitude.isNotEmpty() && latitude.toDoubleOrNull() == null },
                enabled = !isLoading,
            )
            FormField(
                value = longitude,
                onValueChange = { update(null, it) },
                label = stringResource(Res.string.editor_longitude_label),
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                error = stringResource(Res.string.editor_error_coordinate)
                    .takeIf { longitude.isNotEmpty() && longitude.toDoubleOrNull() == null },
                enabled = !isLoading,
            )
        }
    }

    // TODO: Points picker

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

        FormListCreator(
            elements = item.tracks.orEmpty().map { it.editable() },
            onElementsChange = { list ->
                onUpdateItem(item.copy(tracks = list.map(EditableExternalTrack::build)))
            },
            constructor = { EditableExternalTrack() },
            validate = EditableExternalTrack::validate,
            creator = { value, onChange ->
                // FIXME: Cannot modify values
                FormDropdown(
                    selection = value.type,
                    onSelectionChanged = { onChange(value.copy(type = it)) },
                    options = ExternalTrack.Type.entries,
                    label = stringResource(Res.string.editor_external_track_type_label),
                    modifier = Modifier.fillMaxWidth(),
                )
                FormField(
                    value = value.url,
                    onValueChange = { onChange(value.copy(url = it)) },
                    label = stringResource(Res.string.editor_external_track_url_label),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            title = stringResource(Res.string.editor_external_tracks_label),
            elementRender = { (type, url) ->
                if (type == null) return@FormListCreator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(type.icon, null)
                    Text(text = url, modifier = Modifier.padding(start = 4.dp).weight(1f))
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
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
        )

        FormDropdown(
            selection = item.grade,
            onSelectionChanged = { onUpdateItem(item.copy(gradeValue = it.name)) },
            label = stringResource(Res.string.editor_grade_label),
            options = SportsGrade.entries,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        )
        FormDropdown(
            selection = item.ending,
            onSelectionChanged = { onUpdateItem(item.copy(ending = it)) },
            label = stringResource(Res.string.editor_ending_label),
            options = Ending.entries,
            toString = { stringResource(it.displayName) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        )

        // TODO: Pitches picker

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

        // TODO: Description and show picker

        // TODO: Builder picker
        // TODO: Re-builder picker

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
