package sync

import cache.DataCache
import com.russhwolf.settings.set
import database.SettingsKeys
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import network.Backend
import network.connectivityStatus

object DataSync : SyncProcess() {
    /**
     * Synchronizes the data from the server with the local database.
     *
     * @throws IllegalStateException If there's not a valid network connection available.
     */
    override suspend fun synchronize() = try {
        Napier.i { "Waiting until a network is available" }
        if (!connectivityStatus.await(10_000)) {
            error("Network is not available. Won't fetch server")
        }

        Napier.i { "Running data synchronization..." }
        mutableStatus.tryEmit(Status.RUNNING.Indeterminate)

        Napier.d { "Fetching tree from server..." }
        val areas = Backend.tree()

        Napier.d { "Got ${areas.size} areas. Adding them into the database..." }

        DataCache.Areas.insertOrUpdate(areas) { progress, max ->
            mutableStatus.tryEmit(Status.RUNNING(progress / max.toFloat()))
        }
        Napier.i { "All data synchronized with server." }

        settings.set(SettingsKeys.LAST_SYNC, Clock.System.now().toEpochMilliseconds())
    } catch (e: Exception) {
        Napier.e(throwable = e) { "Could not synchronize with server." }
        throw e
    } finally {
        mutableStatus.value = Status.FINISHED
    }
}
