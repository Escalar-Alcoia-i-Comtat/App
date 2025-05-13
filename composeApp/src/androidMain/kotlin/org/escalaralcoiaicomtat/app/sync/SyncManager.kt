package org.escalaralcoiaicomtat.app.sync

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import org.escalaralcoiaicomtat.android.BlockingSyncWorker
import org.escalaralcoiaicomtat.android.DataSyncWorker
import org.escalaralcoiaicomtat.android.applicationContext
import org.escalaralcoiaicomtat.app.data.DataTypes
import java.util.concurrent.TimeUnit

actual object SyncManager {
    private const val PERIODIC_UNIQUE_WORK_NAME = "periodic-data-sync"
    private const val UNIQUE_WORK_NAME = "data-sync"

    private const val PERIODIC_BLOCKING_UNIQUE_WORK_NAME = "periodic-blocking-sync"
    private const val BLOCKING_UNIQUE_WORK_NAME = "blocking-sync"

    actual fun schedule() {
        // In Android, the schedules are handled by the WorkManager
    }

    fun scheduleWorker() {
        val wm = WorkManager.getInstance(applicationContext)

        wm.enqueueUniquePeriodicWork(
            PERIODIC_UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            PeriodicWorkRequestBuilder<DataSyncWorker>(DataSync.SYNC_PERIOD_HOURS, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build(),
        )
        wm.enqueueUniquePeriodicWork(
            PERIODIC_BLOCKING_UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            PeriodicWorkRequestBuilder<BlockingSyncWorker>(BlockingSync.SYNC_PERIOD_HOURS, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build(),
        )
    }

    actual fun run(cause: SyncProcess.Cause, syncId: Pair<DataTypes<*>, Int>?) {
        val wm = WorkManager.getInstance(applicationContext)

        wm.enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<DataSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    workDataOf(
                        SyncProcess.ARG_CAUSE to cause.name,
                        SyncProcess.ARG_TYPE to syncId?.first?.name,
                        SyncProcess.ARG_ID to syncId?.second,
                    )
                )
                .build(),
        )
        if (syncId?.first == DataTypes.Path) {
            wm.enqueueUniqueWork(
                BLOCKING_UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<BlockingSyncWorker>()
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .setInputData(
                        workDataOf(
                            SyncProcess.ARG_CAUSE to cause.name,
                            SyncProcess.ARG_ID to syncId.second,
                        )
                    )
                    .build(),
            )
        }
    }
}
