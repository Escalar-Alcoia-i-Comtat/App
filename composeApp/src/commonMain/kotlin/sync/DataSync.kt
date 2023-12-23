package sync

import com.russhwolf.settings.set
import data.Area
import data.DataType
import data.Path
import data.Sector
import data.Zone
import database.SettingsKeys
import database.database
import database.settings
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import network.Backend
import network.connectivityStatus

object DataSync : SyncProcess() {
    private fun <Type : DataType, RowType : Any> insertOrUpdate(
        value: Type,
        get: (id: Long) -> RowType?,
        insert: (value: Type) -> Unit,
        update: (value: Type) -> Unit
    ) {
        val row = get(value.id)
        if (row == null) {
            // Napier.v { "Inserting ${value::class.simpleName}#${value.id} into database..." }
            insert(value)
        } else {
            // Napier.v { "Updating ${value::class.simpleName}#${value.id} into database..." }
            update(value)
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
        val areas = Backend.tree()
        val zones = areas.flatMap { it.zones }
        val sectors = zones.flatMap { it.sectors }
        val paths = sectors.flatMap { it.paths }

        Napier.d { "Got ${areas.size} areas. Adding them into the database..." }
        database.transaction {
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

                insertOrUpdate(
                    value = area,
                    get = { database.areaQueries.get(it).executeAsOneOrNull() },
                    insert = { i: Area ->
                        database.areaQueries.insert(
                            i.id,
                            i.timestamp,
                            i.displayName,
                            i.image,
                            i.webUrl
                        )
                    },
                    update = { i: Area ->
                        database.areaQueries.update(
                            i.timestamp,
                            i.displayName,
                            i.image,
                            i.webUrl,
                            i.id
                        )
                    }
                )
            }
            for (zone in zones) {
                mutableStatus.value = Status.RUNNING(counter++ / totalSize.toFloat())

                insertOrUpdate(
                    value = zone,
                    get = { database.zoneQueries.get(it).executeAsOneOrNull() },
                    insert = { i: Zone ->
                        with(i) {
                            database.zoneQueries.insert(
                                id,
                                timestamp,
                                displayName,
                                image,
                                webUrl,
                                kmzUUID,
                                point,
                                points,
                                parentAreaId
                            )
                        }
                    },
                    update = { i: Zone ->
                        with(i) {
                            database.zoneQueries.update(
                                timestamp,
                                displayName,
                                image,
                                webUrl,
                                kmzUUID,
                                point,
                                points,
                                parentAreaId,
                                id
                            )
                        }
                    }
                )
            }
            for (sector in sectors) {
                mutableStatus.value = Status.RUNNING(counter++ / totalSize.toFloat())

                insertOrUpdate(
                    value = sector,
                    get = { database.sectorQueries.get(it).executeAsOneOrNull() },
                    insert = { i: Sector ->
                        with(i) {
                            database.sectorQueries.insert(
                                id,
                                timestamp,
                                displayName,
                                image,
                                kidsApt,
                                weight,
                                walkingTime,
                                point,
                                sunTime,
                                parentZoneId
                            )
                        }
                    },
                    update = { i: Sector ->
                        with(i) {
                            database.sectorQueries.update(
                                timestamp,
                                displayName,
                                image,
                                kidsApt,
                                weight,
                                walkingTime,
                                point,
                                sunTime,
                                parentZoneId,
                                id
                            )
                        }
                    }
                )
            }
            for (path in paths) {
                mutableStatus.value = Status.RUNNING(counter++ / totalSize.toFloat())

                insertOrUpdate(
                    value = path,
                    get = { database.pathQueries.get(it).executeAsOneOrNull() },
                    insert = { i: Path ->
                        with(i) {
                            database.pathQueries.insert(
                                id,
                                timestamp,
                                displayName,
                                sketchId,
                                height,
                                grade,
                                ending,
                                pitches,
                                stringCount,
                                paraboltCount,
                                burilCount,
                                pitonCount,
                                spitCount,
                                tensorCount,
                                nutRequired,
                                friendRequired,
                                lanyardRequired,
                                nailRequired,
                                pitonRequired,
                                stapesRequired,
                                showDescription,
                                description,
                                builder,
                                reBuilders,
                                images,
                                parentSectorId
                            )
                        }
                    },
                    update = { i: Path ->
                        with(i) {
                            database.pathQueries.update(
                                timestamp,
                                displayName,
                                sketchId,
                                height,
                                grade,
                                ending,
                                pitches,
                                stringCount,
                                paraboltCount,
                                burilCount,
                                pitonCount,
                                spitCount,
                                tensorCount,
                                nutRequired,
                                friendRequired,
                                lanyardRequired,
                                nailRequired,
                                pitonRequired,
                                stapesRequired,
                                showDescription,
                                description,
                                builder,
                                reBuilders,
                                images,
                                parentSectorId,
                                id
                            )
                        }
                    }
                )
            }
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
