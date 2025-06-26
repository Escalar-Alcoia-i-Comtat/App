package org.escalaralcoiaicomtat.app.sync

import com.russhwolf.settings.set
import io.github.aakira.napier.Napier
import org.escalaralcoiaicomtat.app.database.DatabaseInterface
import org.escalaralcoiaicomtat.app.database.SettingsKeys
import org.escalaralcoiaicomtat.app.database.settings
import org.escalaralcoiaicomtat.app.network.BasicBackend
import kotlin.time.Clock

object BlockingSync : SyncProcess() {
    const val SYNC_PERIOD_HOURS = 4L

    suspend fun start(cause: Cause, pathId: Int? = null) = start(
        mapOf(ARG_CAUSE to cause.name, ARG_ID to pathId)
    )

    override suspend fun SyncContext.synchronize() {
        val cause = getString(ARG_CAUSE)?.let(Cause::valueOf)
        val id = getString(ARG_ID)?.toIntOrNull()

        Napier.i { "Running blocking synchronization..." }
        setStatus(Status.RUNNING.Indeterminate)

        val progress: suspend (current: Long, total: Long) -> Unit = { current, total ->
            setStatus(Status.RUNNING(current.toFloat() / total))
        }

        val blocks = if (id != null) {
            Napier.d { "Fetching blocking from path $id from server..." }
            BasicBackend.blocking(id, progress)
        } else {
            Napier.d { "Fetching all blocks from server..." }
            BasicBackend.blocking(progress)
        }
        Napier.d { "Got ${blocks.size} blocks" }

        setStatus(Status.RUNNING.Indeterminate)

        DatabaseInterface.blocking().updateOrInsert(blocks)

        settings[SettingsKeys.LAST_BLOCK_SYNC_TIME] = Clock.System.now().toEpochMilliseconds()
        settings[SettingsKeys.LAST_BLOCK_SYNC_CAUSE] = cause?.name
    }
}
