package org.escalaralcoiaicomtat.app.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.data.DataTypes
import javax.naming.OperationNotSupportedException

actual object SyncManager {
    actual fun schedule() {
        throw OperationNotSupportedException("Desktop doesn't support scheduling periodic works.")
    }

    actual fun run(cause: SyncProcess.Cause, syncId: Pair<DataTypes<*>, Int>?) {
        CoroutineScope(Dispatchers.IO).launch {
            DataSync.start(cause, syncId)

            if (syncId != null) {
                val (dataType, id) = syncId
                if (dataType == DataTypes.Path) {
                    BlockingSync.start(cause, pathId = id)
                }
            } else {
                BlockingSync.start(cause)
            }
        }
    }
}
