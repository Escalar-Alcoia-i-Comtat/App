package ui.reusable.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import utils.IO

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FormFilePicker(
    file: PlatformFile?,
    onFilePicked: (PlatformFile?) -> Unit,
    type: PickerType,
    label: String,
    modifier: Modifier = Modifier,
    canBeCleared: Boolean = false,
    enabled: Boolean = true,
    fallbackContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(0.dp),
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
                    val pickedFile = FileKit.pickFile(type, PickerMode.Single) ?: return@launch
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
                if (type == PickerType.Image) {
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
        type = PickerType.Image,
        enabled = enabled,
        fallbackContent = if (fallbackImage != null) {
            {
                AsyncImage(fallbackImage, null, Modifier.fillMaxWidth())
            }
        } else null
    )
}
