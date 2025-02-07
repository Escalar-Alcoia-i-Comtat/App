package sync

import com.russhwolf.settings.set
import data.Area
import database.DatabaseInterface
import database.SettingsKeys
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import network.Backend

object DataSync : SyncProcess<List<Area>>() {
    /**
     * Synchronizes the data from the server with the local database.
     *
     * @throws IllegalStateException If there's not a valid network connection available.
     */
    override suspend fun synchronize(): List<Area> = try {
        Napier.i { "Running data synchronization..." }
        mutableStatus.emit(Status.RUNNING.Indeterminate)

        Napier.d { "Fetching tree from server..." }
        val areas = Backend.tree()
        Napier.d { "Got ${areas.size} areas" }

        @Suppress("DEPRECATION") val zones = areas.flatMap { it.zones }
        @Suppress("DEPRECATION") val sectors = zones.flatMap { it.sectors }
        @Suppress("DEPRECATION") val paths = sectors.flatMap { it.paths }

        Napier.d { "Saving areas..." }
        DatabaseInterface.areas().updateOrInsert(areas)
        Napier.d { "Saving zones..." }
        DatabaseInterface.zones().updateOrInsert(zones)
        Napier.d { "Saving sectors..." }
        DatabaseInterface.sectors().updateOrInsert(sectors)
        Napier.d { "Saving paths..." }
        DatabaseInterface.paths().updateOrInsert(paths)

        settings[SettingsKeys.LAST_SYNC] = Clock.System.now().toEpochMilliseconds()

        areas
    } catch (e: Exception) {
        Napier.e(throwable = e) { "Could not synchronize with server." }
        throw e
    } finally {
        mutableStatus.emit(Status.FINISHED)
    }

    val areas = DatabaseInterface.areas().allLive()
    val zones = DatabaseInterface.zones().allLive()
    val sectors = DatabaseInterface.sectors().allLive()
    val paths = DatabaseInterface.paths().allLive()
}
