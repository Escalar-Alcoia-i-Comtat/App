package ui.dialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import resources.MR
import search.Filter
import search.VisibilityFilter

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun SearchFiltersDialog(
    areasFilters: SnapshotStateList<Filter<Any>>,
    zonesFilters: SnapshotStateList<Filter<Any>>,
    sectorsFilters: SnapshotStateList<Filter<Any>>,
    pathsFilters: SnapshotStateList<Filter<Any>>,
    onDismissRequest: () -> Unit
) {
    fun LazyListScope.drawFilters(
        title: StringResource,
        filters: SnapshotStateList<Filter<Any>>
    ) {
        stickyHeader { Text(stringResource(title)) }
        itemsIndexed(filters.toList()) { index, filter ->
            ListItem(
                headlineContent = { filter.Label() },
                trailingContent = {
                    if (filter is VisibilityFilter) {
                        Icon(
                            imageVector = if (filter.value) {
                                Icons.Outlined.RadioButtonChecked
                            } else {
                                Icons.Outlined.RadioButtonUnchecked
                            },
                            contentDescription = null
                        )
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.clickable {
                    filters[index] = filter.toggle()
                }
            )
        }
    }

    // TODO : Localize everything
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Filters") },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) { Text("OK") }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        text = {
            LazyColumn {
                drawFilters(MR.strings.search_filter_areas, areasFilters)
                drawFilters(MR.strings.search_filter_zones, zonesFilters)
                drawFilters(MR.strings.search_filter_sectors, sectorsFilters)
                drawFilters(MR.strings.search_filter_paths, pathsFilters)
            }
        }
    )
}
