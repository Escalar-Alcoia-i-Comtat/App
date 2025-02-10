package ui.screen

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import data.DataType
import data.DataTypeWithImage
import data.DataTypeWithParent
import data.DataTypeWithPoint
import data.DataTypes
import data.Sector
import data.Zone
import data.generic.LatLng
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import org.jetbrains.compose.resources.stringResource
import platform.BackHandler
import ui.model.EditorModel
import ui.model.EditorModel.Companion.FILE_KEY_GPX
import ui.model.EditorModel.Companion.FILE_KEY_IMAGE
import ui.model.EditorModel.Companion.FILE_KEY_KMZ
import ui.reusable.form.FormDropdown
import ui.reusable.form.FormField
import ui.reusable.form.FormFilePicker
import ui.reusable.form.FormImagePicker
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
        onBackRequested = onBackRequested
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
    onBackRequested: () -> Unit
) {
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
                }
            )
        }
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
                EditorContent(item, onUpdateItem, parents, files, onFilePicked, isCreate)
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
) {
    FormField(
        value = if (isCreate) stringResource(Res.string.editor_id_automatic) else item.id.toString(),
        onValueChange = {},
        label = stringResource(Res.string.editor_id_label),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        readOnly = true,
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
        )
    }

    FormField(
        value = item.displayName,
        onValueChange = { onUpdateItem(item.copy(displayName = it)) },
        label = stringResource(Res.string.editor_display_name_label),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    )

    if (item is DataTypeWithImage) {
        FormImagePicker(
            file = files[FILE_KEY_IMAGE],
            onFilePicked = { onFilePicked(FILE_KEY_IMAGE, it) },
            label = stringResource(Res.string.editor_image_label),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            fallbackImage = item.imageUrl(),
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
            )
            FormField(
                value = longitude,
                onValueChange = { update(null, it) },
                label = stringResource(Res.string.editor_longitude_label),
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                error = stringResource(Res.string.editor_error_coordinate)
                    .takeIf { longitude.isNotEmpty() && longitude.toDoubleOrNull() == null },
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
        )
    }

    if (item is Sector) {
        FormFilePicker(
            file = files[FILE_KEY_GPX],
            onFilePicked = { onFilePicked(FILE_KEY_GPX, it) },
            label = stringResource(Res.string.editor_gpx_label),
            type = PickerType.File(listOf("gpx")),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        )
    }

    Spacer(Modifier.height(32.dp))
}
