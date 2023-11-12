package database

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.cash.sqldelight.Query

@Composable
fun <RowType: Any> Query<RowType>.collectAsStateList(default: List<RowType> = emptyList()): State<List<RowType>> {
    val state = remember { mutableStateOf(default) }

    DisposableEffect(this) {
        val listener = Query.Listener {
            // When a new value is fetched, send the update
            state.value = executeAsList()
        }
        addListener(listener)

        // Update the default value with the current list entries
        state.value = executeAsList()

        onDispose {
            removeListener(listener)
        }
    }

    return state
}
