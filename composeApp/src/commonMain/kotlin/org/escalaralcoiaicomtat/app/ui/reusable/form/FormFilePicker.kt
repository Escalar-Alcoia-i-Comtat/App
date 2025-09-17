package org.escalaralcoiaicomtat.app.ui.reusable.form

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.utils.IO
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FormFilePicker(
    file: PlatformFile?,
    onFilePicked: (PlatformFile?) -> Unit,
    type: FileKitType,
    label: String,
    modifier: Modifier = Modifier,
    canBeCleared: Boolean = false,
    enabled: Boolean = true,
    fallbackContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below, 0.dp),
        state = rememberTooltipState(),
        tooltip = {
            PlainTooltip { Text("Click or tap to pick") }
        },
        enableUserInput = enabled,
    ) {
        OutlinedCard(
            enabled = enabled,
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val pickedFile = FileKit.openFilePicker(type, mode = FileKitMode.Single) ?: return@launch
                    onFilePicked(pickedFile)
                }
            },
            modifier = modifier,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                if (canBeCleared) {
                    IconButton(onClick = { onFilePicked(null) }, enabled = enabled) {
                        Icon(Icons.Default.Close, stringResource(Res.string.editor_clear))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            file?.let {
                if (type == FileKitType.Image) {
                    var image by remember { mutableStateOf<ByteArray?>(null) }

                    LaunchedEffect(file) {
                        image = file.readBytes()
                    }

                    image?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } ?: Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 8.dp)
                    )
                }
            } ?: fallbackContent?.invoke(this) ?: Text(
                text = stringResource(Res.string.file_picker_hint),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun FormImagePicker(
    file: PlatformFile?,
    onFilePicked: (PlatformFile?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    canBeCleared: Boolean = false,
    enabled: Boolean = true,
    fallbackImage: Any?
) {
    FormFilePicker(
        file = file,
        onFilePicked = onFilePicked,
        label = label,
        modifier = modifier,
        canBeCleared = canBeCleared,
        type = FileKitType.Image,
        enabled = enabled,
        fallbackContent = if (fallbackImage != null) {
            {
                AsyncImage(fallbackImage, null, Modifier.fillMaxWidth())
            }
        } else null
    )
}
