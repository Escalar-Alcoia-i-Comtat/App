package org.escalaralcoiaicomtat.app.sync

import io.github.aakira.napier.Napier
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.escalaralcoiaicomtat.app.data.DataTypes
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.sync.SyncProcess.Cause

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

    @OptIn(DelicateCoroutinesApi::class)
    actual fun schedule() {
        GlobalScope.launch {
            runIfSchedulePermits(SettingsKeys.LAST_SYNC_TIME, DataSync)
            runIfSchedulePermits(SettingsKeys.LAST_BLOCK_SYNC_TIME, BlockingSync)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    actual fun run(cause: Cause, syncId: Pair<DataTypes<*>, Int>?) {
        GlobalScope.promise<Unit> {
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

    @OptIn(DelicateCoroutinesApi::class)
    actual fun runDataSync(cause: SyncProcess.Cause, syncId: Pair<DataTypes<*>, Int>?) {
        GlobalScope.launch {
            DataSync.start(cause, syncId)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    actual fun runBlockingSync(cause: Cause, pathId: Int?) {
        GlobalScope.launch {
            BlockingSync.start(cause, pathId)
        }
    }
}
