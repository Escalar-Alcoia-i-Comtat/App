package org.escalaralcoiaicomtat.app.ui.reusable.form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import escalaralcoiaicomtat.composeapp.generated.resources.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FormDatePicker(
    date: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectableDates: SelectableDates = DatePickerDefaults.AllDates,
    format: DateTimeFormat<LocalDate> = LocalDate.Formats.ISO,
) {
    var showingDialog by remember { mutableStateOf(false) }
    if (showingDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date?.atStartOfDayIn(TimeZone.currentSystemDefault())?.toEpochMilliseconds(),
            selectableDates = selectableDates,
        )

        DatePickerDialog(
            onDismissRequest = { showingDialog = false },
            confirmButton = {
                TextButton(
                    enabled = datePickerState.selectedDateMillis?.let {
                        selectableDates.isSelectableDate(it)
                    } == true,
                    onClick = {
                        onDateChange(
                            Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date
                        )
                        showingDialog = false
                    }
                ) { Text(stringResource(Res.string.action_confirm)) }
            }
        ) {
            DatePicker(
                state = datePickerState,
            )
        }
    }

    OutlinedTextField(
        value = date?.let { format.format(it) } ?: "",
        onValueChange = { },
        readOnly = true,
        label = { Text(label) },
        modifier = modifier,
        maxLines = 1,
        singleLine = true,
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            showingDialog = true
                        }
                    }
                }
            }
    )
}
