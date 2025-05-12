package org.escalaralcoiaicomtat.app.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.data.DataTypes
import org.escalaralcoiaicomtat.app.utils.IO

actual object SyncManager {
    actual fun schedule() {
        throw IllegalStateException("Desktop doesn't support scheduling periodic works.")
    }

    actual fun run(cause: DataSync.Cause, syncId: Pair<DataTypes<*>, Int>?) {
        CoroutineScope(Dispatchers.IO).launch {
            DataSync.start(cause, syncId)
        }
    }
}
