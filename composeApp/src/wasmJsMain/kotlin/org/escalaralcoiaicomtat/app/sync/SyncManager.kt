package org.escalaralcoiaicomtat.app.sync

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.escalaralcoiaicomtat.app.data.DataTypes

actual object SyncManager {
    actual fun schedule() {
        // Scheduling not supported
    }

    @OptIn(DelicateCoroutinesApi::class)
    actual fun run(cause: DataSync.Cause, syncId: Pair<DataTypes<*>, Int>?) {
        GlobalScope.promise<Unit> {
            DataSync.start(cause, syncId)
        }
    }
}
