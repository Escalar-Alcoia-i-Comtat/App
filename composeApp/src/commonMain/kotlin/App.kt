import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import database.collectAsStateList
import database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import sync.DataSync

@Composable
fun App() {
    MaterialTheme {
        val areas by database.areaQueries
            .getAll()
            .collectAsStateList()

        val zones by database.zoneQueries
            .getAll()
            .collectAsStateList()

        val sectors by database.sectorQueries
            .getAll()
            .collectAsStateList()

        val paths by database.pathQueries
            .getAll()
            .collectAsStateList()

        LaunchedEffect(Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                DataSync.synchronize()
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "There are ${areas.size} areas"
            )
            Text(
                text = "There are ${zones.size} zones"
            )
            Text(
                text = "There are ${sectors.size} sectors"
            )
            Text(
                text = "There are ${paths.size} paths"
            )
        }
    }
}