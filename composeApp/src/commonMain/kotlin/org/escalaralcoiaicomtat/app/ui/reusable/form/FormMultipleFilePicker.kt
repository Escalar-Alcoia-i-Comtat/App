package org.escalaralcoiaicomtat.app.ui.reusable.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.extension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.utils.IO
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FormMultipleFilePicker(
    files: List<PlatformFile>,
    onFilePicked: (PlatformFile) -> Unit,
    onFileRemoved: (PlatformFile) -> Unit,
    type: PickerType,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
) {
    Column(modifier.padding(vertical = 4.dp)) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(0.dp),
            state = rememberTooltipState(),
            tooltip = {
                PlainTooltip { Text(stringResource(Res.string.file_picker_hint)) }
            },
            enableUserInput = enabled,
        ) {
            OutlinedCard(
                enabled = enabled,
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val pickedFiles = FileKit.pickFile(type, PickerMode.Multiple()) ?: return@launch
                        pickedFiles.forEach(onFilePicked)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = if (error != null) {
                    CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                } else {
                    CardDefaults.outlinedCardColors()
                },
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(top = 8.dp),
                )
                Text(
                    text = stringResource(Res.string.file_picker_hint),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp),
                )
            }
        }
        error?.let {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            )
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(files) { file ->
                InputChip(
                    selected = true,
                    onClick = { onFileRemoved(file) },
                    label = { Text(file.name) },
                    avatar = {
                        Icon(
                            imageVector = when (file.extension.lowercase()) {
                                "png", "jpg", "jpeg", "gif", "webp" -> Icons.Default.Image
                                "mp4", "webm" -> Icons.Default.Movie
                                else -> Icons.Default.Description
                            },
                            contentDescription = stringResource(Res.string.file_picker_type_file),
                            modifier = Modifier.size(InputChipDefaults.AvatarSize),
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.file_picker_type_file),
                            modifier = Modifier.size(InputChipDefaults.AvatarSize),
                        )
                    },
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
    }
}
