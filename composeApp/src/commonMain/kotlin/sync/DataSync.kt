package sync

import cache.DataCache
import com.russhwolf.settings.set
import data.DataType
import database.SettingsKeys
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import network.Backend
import network.connectivityStatus

object DataSync : SyncProcess() {
    private suspend fun <Type : DataType> insertOrUpdate(
        value: Type,
        cache: DataCache.Cache<Type>
    ) {
        val row = cache.get(value.id)
        if (row == null) {
            // Napier.v { "Inserting ${value::class.simpleName}#${value.id} into database..." }
            cache.insert(value)
        } else {
            // Napier.v { "Updating ${value::class.simpleName}#${value.id} into database..." }
            cache.update(value)
        }
    }

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
        mutableStatus.value = Status.RUNNING.Indeterminate

        Napier.d { "Fetching tree from server..." }
        val areas = Backend.tree().map { Preprocessors.areaPreprocessor(it) }
        val zones = areas.flatMap { it.zones }.map { Preprocessors.zonePreprocessor(it) }
        val sectors = zones.flatMap { it.sectors }.map { Preprocessors.sectorPreprocessor(it) }
        val paths = sectors.flatMap { it.paths }.map { Preprocessors.pathPreprocessor(it) }

        Napier.d { "Got ${areas.size} areas. Adding them into the database..." }
        val totalSize = areas.sumOf { area ->
            1 + area.zones.sumOf { zone ->
                1 + zone.sectors.sumOf { sector ->
                    1 + sector.paths.size
                }
            }
        }
        var counter = 0

        for (area in areas) {
            mutableStatus.value = Status.RUNNING(counter++ / totalSize.toFloat())

            insertOrUpdate(area, DataCache.Areas)
        }
        for (zone in zones) {
            mutableStatus.value = Status.RUNNING(counter++ / totalSize.toFloat())

            insertOrUpdate(zone, DataCache.Zones)
        }
        for (sector in sectors) {
            mutableStatus.value = Status.RUNNING(counter++ / totalSize.toFloat())

            insertOrUpdate(sector, DataCache.Sectors)
        }
        for (path in paths) {
            mutableStatus.value = Status.RUNNING(counter++ / totalSize.toFloat())

            insertOrUpdate(path, DataCache.Paths)
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
