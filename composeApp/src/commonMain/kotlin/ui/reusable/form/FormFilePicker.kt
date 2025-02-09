package ui.reusable.form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import utils.IO

@Composable
fun FormFilePicker(
    file: PlatformFile?,
    onFilePicked: (PlatformFile?) -> Unit,
    type: PickerType,
    label: String,
    modifier: Modifier = Modifier,
    canBeCleared: Boolean = false,
) {
    OutlinedTextField(
        value = file?.name ?: "",
        onValueChange = {},
        modifier = modifier,
        readOnly = true,
        label = { Text(label) },
        trailingIcon = if (canBeCleared) {
            {
                IconButton(onClick = { onFilePicked(null) }) {
                    Icon(Icons.Default.Close, stringResource(Res.string.editor_clear))
                }
            }
        } else null,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            withContext(Dispatchers.IO) {
                                val pickedFile = FileKit.pickFile(type, PickerMode.Single)
                                    ?: return@withContext
                                onFilePicked(pickedFile)
                            }
                        }
                    }
                }
            }
    )
}
