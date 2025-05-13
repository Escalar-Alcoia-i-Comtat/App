package org.escalaralcoiaicomtat.android

import android.content.Context
import androidx.work.WorkerParameters
import org.escalaralcoiaicomtat.app.sync.DataSync

class DataSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
): SyncWorker(appContext, workerParams) {
    override suspend fun operation(arguments: Map<String, Any?>?) {
        DataSync.start(arguments)
    }
}
