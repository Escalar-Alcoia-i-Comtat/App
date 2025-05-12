package org.escalaralcoiaicomtat.app.sync

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import org.escalaralcoiaicomtat.android.SyncWorker
import org.escalaralcoiaicomtat.android.applicationContext
import org.escalaralcoiaicomtat.app.data.DataTypes
import java.util.concurrent.TimeUnit

actual object SyncManager {
    private const val PERIODIC_UNIQUE_WORK_NAME = "periodic-sync"
    private const val UNIQUE_WORK_NAME = "sync"

    actual fun schedule() {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(4, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            PERIODIC_UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    actual fun run(cause: DataSync.Cause, syncId: Pair<DataTypes<*>, Int>?) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                workDataOf(
                    DataSync.ARG_CAUSE to cause.name,
                    DataSync.ARG_TYPE to syncId?.first?.name,
                    DataSync.ARG_ID to syncId?.second,
                )
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }
}
