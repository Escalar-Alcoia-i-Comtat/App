package ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

@Composable
fun <RowType : Any> Query<RowType>.collectAsStateList(
    initial: List<RowType> = emptyList(),
    context: CoroutineContext = Dispatchers.Default
) = asFlow()
    .mapToList(context)
    .collectAsState(initial)
