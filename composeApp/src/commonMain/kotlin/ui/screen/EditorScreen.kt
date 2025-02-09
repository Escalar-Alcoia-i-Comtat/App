package ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import data.DataType
import data.DataTypeWithImage
import data.DataTypes
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import org.jetbrains.compose.resources.stringResource
import ui.model.EditorModel
import ui.reusable.form.FormField
import ui.reusable.form.FormFilePicker

@Composable
fun <DT : DataType> EditorScreen(
    dataType: DataTypes<DT>,
    id: Long?, // If null, a new item will be created
    model: EditorModel<DT> = viewModel { EditorModel(dataType, id) },
    onBackRequested: () -> Unit
) {
    val item by model.item.collectAsState()
    val file by model.imageFile.collectAsState()

    LaunchedEffect(Unit) { model.load(onBackRequested) }

    EditorScreen(
        item = item,
        onUpdateItem = model::updateItem,
        imageFile = file,
        onImageFilePicked = model::setImageFile,
        isCreate = id == null,
        onBackRequested
    )
}

@Composable
@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalMaterial3Api::class)
fun <DT : DataType> EditorScreen(
    item: DT?,
    onUpdateItem: ((current: DT) -> DT) -> Unit,
    imageFile: PlatformFile?,
    onImageFilePicked: (PlatformFile?) -> Unit,
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
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(8.dp)) {
            FormField(
                value = if (isCreate) stringResource(Res.string.editor_id_automatic) else item.id.toString(),
                onValueChange = {},
                label = stringResource(Res.string.editor_id_label),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                readOnly = true,
            )
            FormField(
                value = item.displayName,
                onValueChange = { v -> onUpdateItem { it.copy(displayName = v) as DT } },
                label = stringResource(Res.string.editor_display_name_label),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            )
            if (item is DataTypeWithImage) {
                FormFilePicker(
                    file = imageFile,
                    onFilePicked = onImageFilePicked,
                    label = stringResource(Res.string.editor_image_label),
                    type = PickerType.Image,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                )
            }
        }
    }
}
