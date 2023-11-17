package database

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import app.cash.sqldelight.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@Composable
fun <RowType: Any> Query<RowType>.collectAsStateList(): State<List<RowType>> {
    val state = remember { mutableStateOf(executeAsList()) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(this) {
        val listener = Query.Listener {
            // When a new value is fetched, send the update
            state.value = executeAsList()
        }
        addListener(listener)

        coroutineScope.launch(Dispatchers.IO) {
            // Update the default value with the current list entries
            state.value = executeAsList()
        }

        onDispose {
            removeListener(listener)
        }
    }

    return state
}
