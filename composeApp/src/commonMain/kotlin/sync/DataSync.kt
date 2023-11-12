package sync

import data.Area
import data.Path
import data.Sector
import data.Zone
import data.model.DataTypeWithDisplayName
import database.database
import io.github.aakira.napier.Napier
import network.Backend

object DataSync : SyncProcess() {
    private fun <Type : DataTypeWithDisplayName, RowType : Any> insertOrUpdate(
        value: Type,
        get: (id: Long) -> RowType?,
        insert: (value: Type) -> Unit,
        update: (value: Type) -> Unit
    ) {
        val row = get(value.id)
        if (row == null) {
            Napier.d { "Inserting ${value::class.simpleName}#${value.id} into database..." }
            insert(value)
        } else {
            Napier.d { "Updating ${value::class.simpleName}#${value.id} into database..." }
            update(value)
        }
    }

    override suspend fun synchronize() = try {
        Napier.i { "Running data synchronization..." }
        mutableStatus.value = Status.RUNNING.Indeterminate

        Napier.d { "Fetching tree from server..." }
        val areas = Backend.tree()

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
                mutableStatus.value = Status.RUNNING(totalSize.toFloat() / counter++)

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

                for (zone in area.zones) {
                    mutableStatus.value = Status.RUNNING(totalSize.toFloat() / counter++)

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

                    for (sector in zone.sectors) {
                        mutableStatus.value = Status.RUNNING(totalSize.toFloat() / counter++)

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

                        for (path in sector.paths) {
                            mutableStatus.value = Status.RUNNING(totalSize.toFloat() / counter++)

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
                }
            }
        }
    } finally {
        mutableStatus.value = Status.FINISHED
    }
}
