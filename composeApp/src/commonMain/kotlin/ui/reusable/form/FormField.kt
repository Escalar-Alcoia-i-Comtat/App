package ui.reusable.form

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import utils.applyIfNotNull

@Composable
fun FormField(
    value: String?,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    thisFocusRequester: FocusRequester? = null,
    nextFocusRequester: FocusRequester? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    fallbackValue: String = "",
    error: String? = null,
    onGo: (() -> Unit)? = null
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = value ?: fallbackValue,
        onValueChange = onValueChange,
        modifier = modifier.applyIfNotNull(thisFocusRequester) { focusRequester(it) },
        singleLine = singleLine,
        enabled = enabled,
        readOnly = readOnly,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            capitalization,
            keyboardType = KeyboardType.Text,
            imeAction = if (nextFocusRequester != null) {
                ImeAction.Next
            } else if(onGo != null) {
                ImeAction.Go
            } else {
                ImeAction.Done
            }
        ),
        keyboardActions = KeyboardActions(
            onNext = { nextFocusRequester?.requestFocus() },
            onDone = { softwareKeyboardController?.hide() },
            onGo = { onGo?.invoke() },
        ),
        isError = error != null,
        supportingText = error?.let {
            { Text(it) } // , color = MaterialTheme.colorScheme.error
        },
        trailingIcon = trailingContent,
    )
}
