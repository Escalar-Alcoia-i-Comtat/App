package sync

import com.russhwolf.settings.set
import data.Area
import data.Path
import data.Sector
import data.Zone
import database.SettingsKeys
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import network.Backend

object DataSync : SyncProcess<List<Area>>() {
    /**
     * Synchronizes the data from the server with the local database.
     *
     * @throws IllegalStateException If there's not a valid network connection available.
     */
    override suspend fun synchronize() = try {
        Napier.i { "Running data synchronization..." }
        mutableStatus.tryEmit(Status.RUNNING.Indeterminate)

        Napier.d { "Fetching tree from server..." }
        val areas = Backend.tree()

        Napier.d { "Got ${areas.size} areas" }

        settings[SettingsKeys.LAST_SYNC] = Clock.System.now().toEpochMilliseconds()

        areas
    } catch (e: Exception) {
        Napier.e(throwable = e) { "Could not synchronize with server." }
        throw e
    } finally {
        mutableStatus.value = Status.FINISHED
    }

    val areas: Flow<List<Area>?> get() = result
    val zones: Flow<List<Zone>?> get() = result.map { areas -> areas?.flatMap { it.zones } }
    val sectors: Flow<List<Sector>?> get() = zones.map { zones -> zones?.flatMap { it.sectors } }
    val paths: Flow<List<Path>?> get() = sectors.map { sectors -> sectors?.flatMap { it.paths } }
}
