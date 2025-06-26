package org.escalaralcoiaicomtat.app.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import escalaralcoiaicomtat.composeapp.generated.resources.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toLocalDateTime
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.data.generic.BlockingRecurrenceYearly
import org.escalaralcoiaicomtat.app.data.generic.BlockingTypes
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormDatePicker
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormDateRangePicker
import org.escalaralcoiaicomtat.app.ui.reusable.form.FormDropdown
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBlockingDialog(
    blocking: Blocking,
    onBlockingChange: (Blocking) -> Unit,
    isLoading: Boolean,
    onDeleteRequested: () -> Unit,
    onSaveRequested: () -> Unit,
    onDismissRequested: () -> Unit,
) {
    val isEdit = blocking.id > 0
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismissRequested() },
        title = {
            Text(
                text = stringResource(
                    if (isEdit) Res.string.editor_blocking_edit_title
                    else Res.string.editor_blocking_create_title
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                FormDropdown(
                    selection = blocking.type,
                    onSelectionChanged = { onBlockingChange(blocking.copy(type = it)) },
                    options = BlockingTypes.entries,
                    label = stringResource(Res.string.editor_blocking_type),
                    modifier = Modifier.fillMaxWidth(),
                    toString = { stringResource(it.displayName) },
                    icon = { it.icon },
                    enabled = !isLoading,
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = blocking.endDate != null,
                        shape = SegmentedButtonDefaults.itemShape(0, 3),
                        onClick = {
                            onBlockingChange(
                                if (blocking.endDate == null) {
                                    blocking.copy(
                                        endDate = Clock.System.now()
                                            .toLocalDateTime(TimeZone.currentSystemDefault()),
                                        recurrence = null,
                                    )
                                } else {
                                    blocking.copy(
                                        endDate = null,
                                        recurrence = null,
                                    )
                                }
                            )
                        },
                        enabled = !isLoading,
                    ) { Text(stringResource(Res.string.editor_blocking_end_date)) }
                    SegmentedButton(
                        selected = blocking.recurrence != null,
                        shape = SegmentedButtonDefaults.itemShape(1, 3),
                        onClick = {
                            onBlockingChange(
                                if (blocking.recurrence == null) {
                                    blocking.copy(
                                        endDate = null,
                                        recurrence = BlockingRecurrenceYearly.new(),
                                    )
                                } else {
                                    blocking.copy(
                                        endDate = null,
                                        recurrence = null,
                                    )
                                }
                            )
                        },
                        enabled = !isLoading,
                    ) { Text(stringResource(Res.string.editor_blocking_recurrence)) }
                    SegmentedButton(
                        selected = blocking.endDate == null && blocking.recurrence == null,
                        shape = SegmentedButtonDefaults.itemShape(2, 3),
                        onClick = {
                            onBlockingChange(
                                blocking.copy(
                                    endDate = null,
                                    recurrence = null,
                                )
                            )
                        },
                        enabled = !isLoading,
                    ) { Text(stringResource(Res.string.editor_blocking_forever)) }
                }
                blocking.endDate?.let { endDate ->
                    FormDatePicker(
                        date = endDate.date,
                        onDateChange = {
                            onBlockingChange(
                                blocking.copy(endDate = it.atTime(0, 0))
                            )
                        },
                        label = stringResource(Res.string.editor_blocking_end_date),
                        modifier = Modifier.fillMaxWidth(),
                        // Only allow future dates
                        selectableDates = futureSelectableDates(),
                        enabled = !isLoading,
                    )
                }
                blocking.recurrence?.let { recurrence ->
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val from = LocalDate(now.year, recurrence.fromMonth, recurrence.fromDay.toInt())
                    val to = LocalDate(now.year, recurrence.toMonth, recurrence.toDay.toInt())
                    FormDateRangePicker(
                        dateRange = from to to,
                        onDateRangeChange = { (from, to) ->
                            onBlockingChange(
                                blocking.copy(
                                    recurrence = BlockingRecurrenceYearly(
                                        from.day.toUShort(),
                                        from.month,
                                        to.day.toUShort(),
                                        to.month,
                                    ),
                                )
                            )
                        },
                        label = stringResource(Res.string.editor_blocking_date_range),
                        modifier = Modifier.fillMaxWidth(),
                        // Only allow future dates
                        selectableDates = futureSelectableDates(),
                        enabled = !isLoading,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSaveRequested,
                enabled = !isLoading,
            ) {
                Text(stringResource(Res.string.action_save))
            }
        },
        dismissButton = if (isEdit) {
            {
                TextButton(
                    onClick = onDeleteRequested,
                    enabled = !isLoading,
                ) {
                    Text(stringResource(Res.string.editor_delete))
                }
            }
        } else null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun futureSelectableDates(
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): SelectableDates {
    return object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return clock.now() < Instant.fromEpochMilliseconds(utcTimeMillis)
        }

        override fun isSelectableYear(year: Int): Boolean {
            val now = clock.now().toLocalDateTime(timeZone)
            return now.year <= year
        }
    }
}
