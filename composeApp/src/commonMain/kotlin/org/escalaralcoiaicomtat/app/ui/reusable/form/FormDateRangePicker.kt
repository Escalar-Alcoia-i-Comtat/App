package org.escalaralcoiaicomtat.app.ui.reusable.form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
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
fun FormDateRangePicker(
    dateRange: Pair<LocalDate, LocalDate>?,
    onDateRangeChange: (Pair<LocalDate, LocalDate>) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectableDates: SelectableDates = DatePickerDefaults.AllDates,
    format: DateTimeFormat<LocalDate> = LocalDate.Formats.ISO,
) {
    var showingDialog by remember { mutableStateOf(false) }
    if (showingDialog) {
        val datePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = dateRange?.first?.atStartOfDayIn(TimeZone.currentSystemDefault())?.toEpochMilliseconds(),
            initialSelectedEndDateMillis = dateRange?.second?.atStartOfDayIn(TimeZone.currentSystemDefault())?.toEpochMilliseconds(),
            selectableDates = selectableDates,
        )

        DatePickerDialog(
            onDismissRequest = { showingDialog = false },
            confirmButton = {
                val start = datePickerState.selectedStartDateMillis
                val end = datePickerState.selectedEndDateMillis
                TextButton(
                    enabled = start != null && end != null &&
                            start < end &&
                            selectableDates.isSelectableDate(start) &&
                            selectableDates.isSelectableDate(end),
                    onClick = {
                        val startDate = Instant.fromEpochMilliseconds(start!!)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                        val endDate = Instant.fromEpochMilliseconds(end!!)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                        onDateRangeChange(startDate to endDate)
                        showingDialog = false
                    }
                ) { Text(stringResource(Res.string.action_confirm)) }
            }
        ) {
            DateRangePicker(
                state = datePickerState,
            )
        }
    }

    OutlinedTextField(
        value = dateRange?.let { (start, end) ->
            format.format(start) + " - " + format.format(end)
        } ?: "",
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
