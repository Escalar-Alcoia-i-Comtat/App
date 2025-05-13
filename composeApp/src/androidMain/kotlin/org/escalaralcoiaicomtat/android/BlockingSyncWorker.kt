package org.escalaralcoiaicomtat.android

import android.content.Context
import androidx.work.WorkerParameters
import org.escalaralcoiaicomtat.app.sync.BlockingSync

class BlockingSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
): SyncWorker(appContext, workerParams) {
    override suspend fun operation(arguments: Map<String, Any?>?) {
        BlockingSync.start(arguments)
    }
}
