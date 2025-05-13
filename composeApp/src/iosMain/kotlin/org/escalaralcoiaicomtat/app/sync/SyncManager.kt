package org.escalaralcoiaicomtat.app.sync

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.escalaralcoiaicomtat.app.data.DataTypes
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.sync.SyncProcess.Cause
import org.escalaralcoiaicomtat.app.utils.IO

actual object SyncManager {
    private suspend fun runIfSchedulePermits(
        settingsKey: String,
        process: SyncProcess,
    ) {
        val lastSync = settings
            .getLongOrNull(settingsKey)
            ?.let { Instant.fromEpochMilliseconds(it) }
        val now = Clock.System.now()

        // Synchronize if never synced, or every 12 hours
        if (lastSync == null || (now - lastSync).inWholeHours > DataSync.SYNC_PERIOD_HOURS) {
            process.start(mapOf(SyncProcess.ARG_TYPE to Cause.Scheduled))
        } else {
            Napier.d { "Won't run synchronization ($settingsKey). Last run: ${(now - lastSync).inWholeHours} hours ago" }
        }
    }

    actual fun schedule() {
        CoroutineScope(Dispatchers.IO).launch {
            runIfSchedulePermits(SettingsKeys.LAST_SYNC_TIME, DataSync)
            runIfSchedulePermits(SettingsKeys.LAST_BLOCK_SYNC_TIME, BlockingSync)
        }
    }

    actual fun run(cause: Cause, syncId: Pair<DataTypes<*>, Int>?) {
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
